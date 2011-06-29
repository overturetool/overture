package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.definitions.CustomClassDefinition;
import com.lausdahl.ast.creator.definitions.Field;

public class CustomSetMethod extends Method
{
	Field f;
	CustomClassDefinition c;
	public CustomSetMethod(CustomClassDefinition c, Field f,Environment env)
	{
		super(c,env);
		this.c = c;
		this.f = f;
	}

	@Override
	protected void prepare()
	{
		this.name = "set"
				+ CommonTreeClassDefinition.javaClassName(f.getName());
		this.arguments.add(new Argument(f.getMethodArgumentType(), "value"));

		/**
		 * Sets the {@code left} child of this {@link ABinopExp} node.
		 * 
		 * @param value
		 *            the new {@code left} child of this {@link ABinopExp} node
		 */
		StringBuilder sbDoc = new StringBuilder();
		sbDoc.append("\t");
		sbDoc.append("/**\n");
		sbDoc.append("\t");
		sbDoc.append("* Sets the {@code " + f.getName()
				+ "} child of this {@link " + c.getName() + "} node.\n");
		sbDoc.append("\t* @param value the new {@code " + f.getName()
				+ "} child of this {@link " + c.getName() + "} node\n");

		StringBuilder sb = new StringBuilder();

	
		if (c.getName().equals("TypeChecker") || c.getName().equals("Eval"))
		{
			sb.append("\t\tif (value != null) {\n");
			sb.append("\t\t\tvalue.parent(this);\n");
			sb.append("\t\t}\n");
			sb.append("\t\tthis." + f.getName() + " = value;");
		} else if (f.isTokenField)
		{
			sb.append("\t\tthis." + f.getName() + " = value;");
		}

		sbDoc.append("\t*/");
		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}
}
