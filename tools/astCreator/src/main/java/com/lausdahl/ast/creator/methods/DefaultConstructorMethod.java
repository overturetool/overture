package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class DefaultConstructorMethod extends Method
{
	public DefaultConstructorMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
	}

	@Override
	protected void prepare()
	{
		IClassDefinition c = classDefinition;
		this.name = c.getSignatureName();
		this.returnType = "";

		javaDoc = "\t/**\n";
		javaDoc += "\t * Creates a new {@link "+c.getName()+"} node with no children.\n";
		javaDoc += "\t */";
		this.body = "";
	}
}
