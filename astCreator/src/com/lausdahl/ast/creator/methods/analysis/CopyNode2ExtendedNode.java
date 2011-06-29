package com.lausdahl.ast.creator.methods.analysis;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.ExternalJavaClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.definitions.InterfaceDefinition;
import com.lausdahl.ast.creator.methods.GetMethod;
import com.lausdahl.ast.creator.methods.Method;

public class CopyNode2ExtendedNode extends Method
{
	IClassDefinition destination;
	Environment env;
	Environment envDest;

	public CopyNode2ExtendedNode()
	{
		super(null, null);
	}

	public CopyNode2ExtendedNode(IClassDefinition source,
			IClassDefinition destination, Environment env, Environment envDest)
	{
		super(source, env);
		this.destination = destination;
		this.env = env;
		this.envDest = envDest;
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");
		sb.append("\t* Called by the {@link " + c.getName()
				+ "} node from {@link " + c.getName() + "#apply(Switch)}.\n");
		sb.append("\t* @param node the calling {@link " + c.getName()
				+ "} node\n");
		sb.append("\t*/");
		this.javaDoc = sb.toString();
		this.name = "case" + InterfaceDefinition.javaClassName(c.getName());
		this.arguments.add(new Argument(c.getName(), "node"));
		this.annotation = "@SuppressWarnings(\"unchecked\")";
		this.returnType = envDest.node.getSignatureName();
		StringBuilder bodySb = new StringBuilder();

		if (c instanceof ExternalJavaClassDefinition)
		{
this.body = "\t\treturn null;//TODO";
		} else
		{

			bodySb.append("\t\treturn new " + destination.getSignatureName()
					+ "(");
			for (int i = 0; i < c.getFields().size(); i++)
			{
				Field sourceField = c.getFields().get(i);
				Method getMethod = new GetMethod(c, sourceField, env);
				getMethod.getJavaSourceCode();
				String getMethodName = getMethod.name;
				String getter = "node." + getMethodName + "()";
				if (!sourceField.isTokenField)
				{
					if (!sourceField.isList)
					{
						bodySb.append("("
								+ destination.getFields().get(i).getType()
								+ ")" + getter + ".apply(this),");
					} else
					{
						bodySb.append("copyList(" + getter + "),");
					}
				} else
				{
					bodySb.append(getter + ",");
				}
			}
			int diff = destination.getFields().size() - c.getFields().size();
			if (diff > 0)
			{
				for (int i = 0; i < diff; i++)
				{
					bodySb.append("null,");
				}
			}

			if (bodySb.toString().endsWith(","))
			{
				bodySb.delete(bodySb.length() - 1, bodySb.length());
			}
			bodySb.append(");");

			this.body = bodySb.toString();
		}
	}
}
