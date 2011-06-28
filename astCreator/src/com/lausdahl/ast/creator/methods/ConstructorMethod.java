package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.Field;
import com.lausdahl.ast.creator.IClassDefinition;

public class ConstructorMethod extends Method
{
	public ConstructorMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
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

			sbDoc.append("\t* @param " + name + " the {@link " + f.getType()
					+ "} node for the {@code " + name
					+ "} child of this {@link " + classDefinition.getName()
					+ "} node\n");
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


}
