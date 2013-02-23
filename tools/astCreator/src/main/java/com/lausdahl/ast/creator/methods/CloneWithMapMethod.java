package com.lausdahl.ast.creator.methods;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.lausdahl.ast.creator.definitions.ExternalEnumJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.Field.StructureType;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;
import com.lausdahl.ast.creator.definitions.JavaTypes;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.NameUtil;

public class CloneWithMapMethod extends CloneMethod
{
	@Override
	protected void prepare(Environment env)
	{
		IClassDefinition c = classDefinition;
		this.name = "clone";

		this.returnType = getSpecializedTypeName(c,env);
		this.requiredImports.add("java.util.Map");

		this.arguments.add(new Argument("Map<" + env.iNode.getName().getName() + ","
				+ env.iNode.getName().getName() + ">", "oldToNewMap"));

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t/**\n");
		sbDoc.append("\t * Creates a deep clone of this {@link " + c.getName().getName()
				+ "} node while putting all\n");
		sbDoc.append("\t * old node-new node relations in the map {@code oldToNewMap}.\n");
		sbDoc.append("\t * @param oldToNewMap the map filled with the old node-new node relation\n");
		sbDoc.append("\t * @return a deep clone of this {@link " + c.getName().getName()
				+ "} node\n");
		sbDoc.append("\t */");

		StringBuilder sb = new StringBuilder();

		List<Field> fields = new Vector<Field>();

		for (Field field :  classDefinition.getInheritedFields())
		{
			if (!classDefinition.refinesField(field.getName(env),env))
			{
				fields.add(field);
			}
		}

		fields.addAll(c.getFields());

		switch (classType)
		{
			case Production:
			case SubProduction:
				this.annotation = "@Override";
				this.isAbstract = true;
				break;
			case Alternative:
			case Custom:
			case Unknown:
				sb.append("\t\t" + c.getName().getName() + " node = new " + c.getName().getName()
						+ "(\n");

				if (!fields.isEmpty())
				{
					String tmp = "";
					for (Field f : fields)
					{
						String name = f.getName(env);

						if (classDefinition.isRefinedField(f,env))
						{
							name = f.getCast(env) + name;
						}

						if (f.structureType == StructureType.Graph)
						{
							tmp += ("\t\t\t" + name + ",\n");
						} else if (f.isList && !f.isDoubleList)
						{
							tmp += ("\t\t\tcloneList"
									+ (f.isTypeExternalNotNode() ? "External"
											: "") + "(" + name + ", oldToNewMap),\n");
						} else if (f.isDoubleList)
						{
							tmp += ("\t\t\tcloneListList(" + name + ", oldToNewMap),\n");
						} else
						{

							if (JavaTypes.isPrimitiveType(f.getType(env))
									|| f.type instanceof ExternalEnumJavaClassDefinition)
							{
								tmp += ("\t\t\t" + name + ",\n");
							} else
							{
								tmp += ("\t\t\tcloneNode(" + name + ", oldToNewMap),\n");
							}
						}
					}
					sb.append(tmp.substring(0, tmp.length() - 2) + "\n");
				}
				sb.append("\t\t);\n");
				sb.append("\t\toldToNewMap.put(this, node);\n");
				sb.append("\t\treturn node;");
				break;
			case Token:
				sb.append("\t\t" + c.getName().getName() + " token = new " + c.getName().getName()
						+ "( ");

				if (!fields.isEmpty())
				{
					String tmp = "";
					for (Field f : fields)
					{
						String name = f.getName(env);

						if (classDefinition.isRefinedField(f,env))
						{
							name = f.getCast(env) + name;
						}
						tmp += ("get"
								+ NameUtil.getClassName(name) + "(), ");
					}
					sb.append(tmp.substring(0, tmp.length() - 2));
				}

				sb.append(");\n");
				sb.append("\t\toldToNewMap.put(this, token);\n");
				sb.append("\t\treturn token;");

				break;

		}

		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}

	public CloneWithMapMethod(IClassDefinition c, ClassType classType)
	{
		super(c, classType);
	}

	@Override
	protected void prepareVdm(Environment env)
	{

		skip = true;

		IClassDefinition c = classDefinition;
		this.name = "clone";

		this.returnType = c.getName().getName();
		// this.requiredImports.add("java.util.LinkedList");
		// this.requiredImports.add("java.util.List");
		this.requiredImports.add("java.util.Map");

		this.arguments.add(new Argument("Map/*<Node,Node>*/", "oldToNewMap"));

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t/**\n");
		sbDoc.append("\t**\n");
		sbDoc.append("\t * Creates a deep clone of this {@link " + c.getName()
				+ "} node while putting all\n");
		sbDoc.append("\t * old node-new node relations in the map {@code oldToNewMap}.\n");
		sbDoc.append("\t * @param oldToNewMap the map filled with the old node-new node relation\n");
		sbDoc.append("\t * @return a deep clone of this {@link " + c.getName()
				+ "} node\n");
		sbDoc.append("\t */");

		StringBuilder sb = new StringBuilder();
		switch (classType)
		{
			case Alternative:
			case Custom:
			case Production:
			case Unknown:
				sb.append("\t\tdcl node : " + c.getName() + " := new "
						+ c.getName() + "(\n");

				if (!c.getFields().isEmpty())
				{
					String tmp = "";
					for (Field f : c.getFields())
					{
						String name = f.getName(env);

						if (classDefinition.isRefinedField(f,env))
						{
							name = f.getCast(env) + name;
						}
						if (f.isList)
						{
							tmp += ("\t\t\tcloneList(" + name + ", oldToNewMap),\n");
						} else
						{
							tmp += ("\t\t\tcloneNode(" + name + ", oldToNewMap),\n");
						}
					}
					sb.append(tmp.substring(0, tmp.length() - 2) + "\n");
				}
				sb.append("\t\t);\n");
				sb.append("\t\toldToNewMap.put(this, node);\n");
				sb.append("\t\treturn node;");
				break;
			case Token:
				sb.append("\t\tdcl node : " + c.getName() + " := new "
						+ c.getName() + "(");

				if (!c.getFields().isEmpty())
				{
					String tmp = "";
					for (Field f : c.getFields())
					{
						String name = f.getName(env);
						if (classDefinition.isRefinedField(f,env))
						{
							name = f.getCast(env) + name;
						}
						tmp += ("get"
								+ NameUtil.getClassName(name) + "(), ");
					}
					sb.append(tmp.substring(0, tmp.length() - 2));
				}

				sb.append(");\n");
				sb.append("\t\toldToNewMap.put(this, token);\n");
				sb.append("\t\treturn token;");

				break;
			default:
				break;
		}

		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}

	@Override
	public Set<String> getRequiredImports(Environment env)
	{
		Set<String> imports = new HashSet<String>();
		imports.addAll(super.getRequiredImports(env));
		imports.add(env.iNode.getName().getCanonicalName());
		imports.add("java.util.Map");
		return imports;
	}
}
