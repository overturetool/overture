package com.lausdahl.ast.creator.methods;

import java.util.List;
import java.util.Vector;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.JavaTypes;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;

public class CloneWithMapMethod extends CloneMethod
{
	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		this.name = "clone";

		this.returnType = c.getName();
//		this.requiredImports.add("java.util.LinkedList");
//		this.requiredImports.add("java.util.List");
		this.requiredImports.add("java.util.Map");

		this.arguments.add(new Argument("Map<"+env.node.getName()+","+env.node.getName()+">", "oldToNewMap"));

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t/**\n");
		sbDoc.append("\t * Creates a deep clone of this {@link " + c.getName()
				+ "} node while putting all\n");
		sbDoc.append("\t * old node-new node relations in the map {@code oldToNewMap}.\n");
		sbDoc.append("\t * @param oldToNewMap the map filled with the old node-new node relation\n");
		sbDoc.append("\t * @return a deep clone of this {@link " + c.getName()
				+ "} node\n");
		sbDoc.append("\t */");

		StringBuilder sb = new StringBuilder();
		
		List<Field> fields = new Vector<Field>();
		if(classDefinition instanceof CommonTreeClassDefinition)
		{
			fields.addAll(((CommonTreeClassDefinition)classDefinition).getInheritedFields());
		}
		fields.addAll(c.getFields());
		
		switch (classType)
		{
			case Alternative:
			case Custom:
			case Production:
			case Unknown:
				sb.append("\t\t" + c.getName() + " node = new " + c.getName()
						+ "(\n");

				if (!c.getFields().isEmpty())
				{
					String tmp = "";
					for (Field f : fields)
					{
						String name = f.getName();
						if (f.isList)
						{
							tmp += ("\t\t\tcloneList(" + name + ", oldToNewMap),\n");
						} else
						{
							
							if (JavaTypes.isPrimitiveType(f.getType()))
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
				sb.append("\t\t"+c.getName()+" token = new " + c.getName() + "( ");

				if (!c.getFields().isEmpty())
				{
					String tmp = "";
					for (Field f : fields)
					{
						tmp += ("get"
								+ CommonTreeClassDefinition.javaClassName(f.getName()) + "(), ");
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

	public CloneWithMapMethod(IClassDefinition c, ClassType classType,Environment env)
	{
		super(c, classType,env);
	}
	
	@Override
	protected void prepareVdm()
	{
		
		skip = true;
		
		
		
		IClassDefinition c = classDefinition;
		this.name = "clone";

		this.returnType = c.getName();
//		this.requiredImports.add("java.util.LinkedList");
//		this.requiredImports.add("java.util.List");
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
				sb.append("\t\tdcl node : " + c.getName() + " := new " + c.getName()
						+ "(\n");

				if (!c.getFields().isEmpty())
				{
					String tmp = "";
					for (Field f : c.getFields())
					{
						String name = f.getName();
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
				sb.append("\t\tdcl node : " + c.getName() + " := new " + c.getName()
						+ "(");

				if (!c.getFields().isEmpty())
				{
					String tmp = "";
					for (Field f : c.getFields())
					{
						tmp += ("get"
								+ CommonTreeClassDefinition.javaClassName(f.getName()) + "(), ");
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
}
