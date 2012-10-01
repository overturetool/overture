package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.utils.NameUtil;

public class CustomSetMethod extends Method
{
	Field f;

	public CustomSetMethod(IClassDefinition c, Field f)
	{
		super(c);
		this.f = f;
	}

	@Override
	protected void prepare(Environment env)
	{
		this.name = "set"
				+ NameUtil.javaClassName(f.getName());
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
				+ "} child of this {@link " + classDefinition.getName().getName() + "} node.\n");
		sbDoc.append("\t* @param value the new {@code " + f.getName()
				+ "} child of this {@link " + classDefinition.getName().getName() + "} node\n");

		StringBuilder sb = new StringBuilder();

		sb.append("\t\tthis." + f.getName() + " = value;");

		sbDoc.append("\t*/");
		this.javaDoc = sbDoc.toString();
		this.body = sb.toString();
	}
}
