package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;

public class DefaultConstructorMethod extends Method
{
	public DefaultConstructorMethod(IClassDefinition c)
	{
		super(c);
		isConstructor=true;
	}

	@Override
	protected void prepare(Environment env)
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
