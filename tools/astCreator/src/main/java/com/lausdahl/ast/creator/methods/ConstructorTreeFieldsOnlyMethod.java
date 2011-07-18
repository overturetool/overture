package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.Field.StructureType;

public class ConstructorTreeFieldsOnlyMethod extends Method
{
	public ConstructorTreeFieldsOnlyMethod(IClassDefinition c, Environment env)
	{
		super(c, env);
	}

	@Override
	protected void prepare()
	{
		skip = true;
//		skip = classDefinition.getFields().isEmpty();
//		if(!skip)
		{
			List<Field> allFields = new Vector<Field>();
			if (classDefinition instanceof CommonTreeClassDefinition)
			{
				allFields.addAll(((CommonTreeClassDefinition) classDefinition).getInheritedFields());
			}
			allFields.addAll(classDefinition.getFields());
			
			for (Field field : allFields)
			{
				if(field.structureType==StructureType.Graph)
				{
					skip = false;
					break;
				}
			}
		}
		if(skip)
		{
			return;
		}
		this.name = classDefinition.getSignatureName();

		this.returnType = "";

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t");
		sbDoc.append("/**\n");
		sbDoc.append("\t");
		sbDoc.append("* Creates a new {@code " + classDefinition.getName()
				+ "} node with the given nodes as children.\n");
		sbDoc.append("\t");
		sbDoc.append("* The basic child nodes are removed from their previous parents.\n");

		StringBuilder sb = new StringBuilder();

		if (classDefinition instanceof CommonTreeClassDefinition)
		{
			sb.append("\t\tsuper(");
			List<Field> fields = new Vector<Field>();
			fields.addAll(((CommonTreeClassDefinition) classDefinition).getInheritedFields());
			skip = skip && fields.isEmpty();
			for (Field f : fields)
			{
				if (f.structureType == StructureType.Tree)
				{
					String name = f.getName().replaceAll("_", "") + "_";
					this.arguments.add(new Argument(f.getMethodArgumentType(), name));
					sb.append(name + ",");
				} else
				{
					sb.append("null" + ",");
				}
			}
			if (!fields.isEmpty())
			{
				sb.delete(sb.length() - 1, sb.length());
			}
			sb.append(");\n");

		}

		for (Field f : classDefinition.getFields())
		{
			if (f.structureType == StructureType.Tree)
			{
				String name = f.getName().replaceAll("_", "");
				this.arguments.add(new Argument(f.getMethodArgumentType(), name
						+ "_"));
				sb.append("\t\t");
				sb.append("this.set");
				sb.append(CommonTreeClassDefinition.javaClassName(f.getName()));
				sb.append("(");
				sb.append(name + "_");
				sb.append(");\n");

				sbDoc.append("\t* @param " + name + " the {@link "
						+ f.getType() + "} node for the {@code " + name
						+ "} child of this {@link " + classDefinition.getName()
						+ "} node\n");
			}
		}

		sbDoc.append("\t*/");
		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}

	@Override
	public String toString()
	{
		if (classDefinition.getFields().isEmpty())
		{
			return "";
		}
		return super.toString();
	}

	@Override
	public Set<String> getRequiredImports()
	{
		Set<String> list = new HashSet<String>();
		list.addAll(super.getRequiredImports());
		if (classDefinition instanceof CommonTreeClassDefinition)
		{
			List<Field> fields = new Vector<Field>();
			fields.addAll(((CommonTreeClassDefinition) classDefinition).getInheritedFields());
			for (Field field : fields)
			{
				list.addAll(field.getRequiredImports());
			}
		}

		String nodelistpackage = env.nodeList.getPackageName() + "."
				+ env.nodeList.getSignatureName();
		list.remove(nodelistpackage);

		return list;
	}

}
