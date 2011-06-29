package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.IClassDefinition.ClassType;

public class CloneMethod extends Method
{
	ClassType classType;

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		this.name = "clone";

		this.returnType = c.getName();
//		this.requiredImports.add("java.util.LinkedList");
//		this.requiredImports.add("java.util.List");
		this.requiredImports.add("java.util.Map");

		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t/**\n");
		sbDoc.append("\t * Returns a deep clone of this {@link " + c.getName()
				+ "} node.\n");
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
				sb.append("\t\treturn new " + c.getName() + "(\n");

				if (!c.getFields().isEmpty())
				{
					String tmp = "";
					for (Field f : c.getFields())
					{
						String name =  f.getName();
						// this.arguments.add(new Argument(f.getType(), name + "_"));

						if (f.isList)
						{
							tmp += ("\t\t\tcloneList(" + name + "),\n");
						} else
						{
							tmp += ("\t\t\tcloneNode(" + name + "),\n");
						}
					}
					sb.append(tmp.substring(0, tmp.length() - 2) + "\n");
				}

				sb.append("\t\t);");
				break;

			case Token:
				sb.append("\t\treturn new " + c.getName() + "( ");

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

				sb.append(");");

				break;

		}

		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}

	public CloneMethod(IClassDefinition c, ClassType classType,Environment env)
	{
		super(c,env);
		this.classType = classType;
	}
	
	@Override
	protected void prepareVdm()
	{
	skip = true;
	}
}
