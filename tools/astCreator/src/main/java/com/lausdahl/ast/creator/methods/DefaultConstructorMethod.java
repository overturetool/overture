package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;

public class DefaultConstructorMethod extends Method
{
	public DefaultConstructorMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
		isConstructor=true;
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		this.name = c.getName().getName();
		this.returnType = "";

		javaDoc = "\t/**\n";
		javaDoc += "\t * Creates a new {@link "+c.getName().getName()+"} node with no children.\n";
		javaDoc += "\t */";
		this.body = "";
	}
}
