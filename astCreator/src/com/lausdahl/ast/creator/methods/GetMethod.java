package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.CommonTreeClassDefinition;
import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.Field;
import com.lausdahl.ast.creator.IClassDefinition;

public class GetMethod extends Method
{
	Field f;

	public GetMethod(IClassDefinition c, Field f,Environment env)
	{
		super(c,env);
		this.f = f;
	}

	@Override
	protected void prepare()
	{

		javaDoc = "\t/**\n";
		javaDoc += "\t * Returns the {@link "+f.getType()+"} node which is the {@code "+f.getName()+"} child of this {@link "+classDefinition.getName()+"} node.\n";
		javaDoc += "\t * @return the {@link "+f.getType()+"} node which is the {@code "+f.getName()+"} child of this {@link "+classDefinition.getName()+"} node\n";
		javaDoc += "\t*/";

		this.name = "get"
				+ CommonTreeClassDefinition.javaClassName(f.getName());
		// this.arguments.add(new Argument(f.getType(), "value"));
		this.returnType = f.getType();
		StringBuilder sb = new StringBuilder();

		sb.append("\t\treturn this." + f.getName() + ";");

		this.body = sb.toString();
	}
}
