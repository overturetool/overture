package com.lausdahl.ast.creator.methods;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.Field.StructureType;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.JavaTypes;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.NameUtil;

public class ConstructorTreeFieldsOnlyMethod extends ConstructorMethod
{
	public ConstructorTreeFieldsOnlyMethod(IClassDefinition c)
	{
		super(c);
		this.name = classDefinition.getName().getName();
	}

	@Override
	protected void prepare(Environment env)
	{
		skip = true;
		// skip = classDefinition.getFields().isEmpty();
		// if(!skip)
		{
			List<Field> allFields = new Vector<Field>();
			if (env.isTreeNode(classDefinition ))
			{
				allFields.addAll(classDefinition.getInheritedFields());
			}
			allFields.addAll(classDefinition.getFields());

			for (Field field : allFields)
			{
				if (field.structureType == StructureType.Graph)
				{
					skip = false;
					break;
				}
			}
		}
		if (skip)
		{
			return;
		}
		this.name = classDefinition.getName().getName();

		this.returnType = "";

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t");
		sbDoc.append("/**\n");
		sbDoc.append("\t");
		sbDoc.append("* Creates a new {@code " + classDefinition.getName()
				+ "} node with the given nodes as children.\n");
		sbDoc.append("\t");
		sbDoc.append("* @deprecated This method should not be used, use AstFactory instead.");
		sbDoc.append("\n\t");
		sbDoc.append("* The basic child nodes are removed from their previous parents.\n");

		StringBuilder sb = new StringBuilder();

		sb.append("\t\tsuper(");
		List<Field> fields = new Vector<Field>();
		fields.addAll(classDefinition.getInheritedFields());
		skip = skip && fields.isEmpty();
		for (Field f : fields)
		{
			if (classDefinition.refinesField(f.getName(env),env))
			{
				// This field is refined in the sub class, so skip it and null the super class field.
				sb.append(JavaTypes.getDefaultValue(f.getType(env))+",");
			} else
			{
				if (f.structureType == StructureType.Tree)
				{
					String name = f.getName(env).replaceAll("_", "") + "_";
					this.arguments.add(new Argument(f.getMethodArgumentType(env), name));
					sb.append(name + ",");
				} else
				{
					sb.append(JavaTypes.getDefaultValue(f.getType(env))+",");
				}
			}
		}
		if (!fields.isEmpty())
		{
			sb.delete(sb.length() - 1, sb.length());
		}
		sb.append(");\n");

		for (Field f : classDefinition.getFields())
		{
			if (f.structureType == StructureType.Tree)
			{
				String name = f.getName(env).replaceAll("_", "");
				this.arguments.add(new Argument(f.getMethodArgumentType(env), name
						+ "_"));
				sb.append("\t\t");
				sb.append("this.set");
				sb.append(NameUtil.javaClassName(f.getName(env)));
				sb.append("(");
				sb.append(name + "_");
				sb.append(");\n");

				sbDoc.append("\t* @param " + name + "_ the {@link "
						+ NameUtil.stripGenerics(f.getType(env)) + "} node for the {@code " + name
						+ "} child of this {@link " + classDefinition.getName().getName()
						+ "} node\n");
			}else if(JavaTypes.isPrimitiveType(f.getType(env)))
			{
				sb.append("\t\t");
				sb.append("this.set");
				sb.append(NameUtil.javaClassName(f.getName(env)));
				sb.append("(");
				sb.append(JavaTypes.getDefaultValue(f.getType(env)));
				sb.append(");\n");

//				sbDoc.append("\t* @param " + name + " the {@link "
//						+ f.getType() + "} node for the {@code " + name
//						+ "} child of this {@link " + classDefinition.getName()
//						+ "} node\n");
			}
		}

		sbDoc.append("\t*/");
		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}

}
