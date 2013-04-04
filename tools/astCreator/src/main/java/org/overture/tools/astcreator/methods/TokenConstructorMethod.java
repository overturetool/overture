package org.overture.tools.astcreator.methods;

import org.overture.tools.astcreator.definitions.Field;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;

public class TokenConstructorMethod extends Method
{

	private String tokenName;
	private Field f;


	public TokenConstructorMethod(IClassDefinition c,Field f, String tokenName)
	{
		super(c);
		this.f = f;
		this.tokenName = tokenName;
	}

	
	@Override
	protected void prepare(Environment env)
	{
		isConstructor = true;
		name = classDefinition.getName().getName();
		returnType = "";
		body = "\t\t" + f.getName(env)
				+ " = \""
				+ tokenName + "\";";
	}
	
	@Override
	protected void prepareVdm(Environment env)
	{
		isConstructor = true;
		name = classDefinition.getName().getName();
		returnType = "";
		body = "\t\t" + f.getName(env)
				+ " := \""
				+ tokenName + "\";";
	}
}
