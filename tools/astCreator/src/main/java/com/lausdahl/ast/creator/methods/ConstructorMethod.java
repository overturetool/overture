package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.Field.StructureType;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class ConstructorMethod extends Method
{
	public ConstructorMethod(IClassDefinition c, Environment env)
	{
		super(c, env);
	}

	@Override
	protected void prepare()
	{
		skip = classDefinition.getFields().isEmpty();
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
				if (classDefinition.refinesField(f.getName()))
				{
					// This field is refined in the sub class, so skip it and null the super class field.
					sb.append("null,");
				} else
				{
					String name = f.getName().replaceAll("_", "") + "_";
					this.arguments.add(new Argument(f.getMethodArgumentType(), name));
					sb.append(name + ",");
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
			String name = f.getName().replaceAll("_", "");
			this.arguments.add(new Argument(f.getMethodArgumentType(), name
					+ "_"));
			sb.append("\t\t");
			sb.append("this.set");
			sb.append(CommonTreeClassDefinition.javaClassName(f.getName()));
			sb.append("(");
			sb.append(name + "_");
			sb.append(");\n");
			if (f.structureType == StructureType.Tree)
			{
				sbDoc.append("\t* @param " + name + " the {@link "
						+ f.getType() + "} node for the {@code " + name
						+ "} child of this {@link " + classDefinition.getName()
						+ "} node\n");
			} else
			{
				sbDoc.append("\t* @param "
						+ name
						+ " the {@link "
						+ f.getType()
						+ "} <b>graph</a> node for the {@code "
						+ name
						+ "} child of this {@link "
						+ classDefinition.getName()
						+ "} node.\n\t*  <i>The parent of this {@ "
						+ name
						+ " } will not be changed by adding it to this node.</i>\n");

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

				if (classDefinition.refinesField(field.getName()))
				{
					continue;
				}

				list.addAll(field.getRequiredImports());
				if (field.isList && !field.isDoubleList)
				{
					list.add(Environment.listDef.getImportName());
				}
				if (field.isDoubleList)
				{
					list.add(Environment.collectionDef.getImportName());
					list.add(Environment.listDef.getImportName());
				}
			}

			List<String> removeImportsFromSuper = new Vector<String>();
			removeImportsFromSuper.add(env.graphNodeList.getImportName());
			removeImportsFromSuper.add(env.graphNodeListList.getImportName());
			removeImportsFromSuper.add(env.nodeList.getImportName());
			removeImportsFromSuper.add(env.nodeListList.getImportName());

			list.removeAll(removeImportsFromSuper);

			for (Field field : classDefinition.getFields())
			{
				list.addAll(field.getRequiredImports());
				if (field.isList && !field.isDoubleList)
				{
					list.add(Environment.listDef.getImportName());
				}
				if (field.isDoubleList)
				{
					list.add(Environment.collectionDef.getImportName());
					list.add(Environment.listDef.getImportName());
				}
			}
		}

		String nodelistpackage = env.nodeList.getImportName();
		list.remove(nodelistpackage);

		return list;
	}

}
