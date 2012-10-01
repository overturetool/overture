package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;

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
		body = "\t\t" + f.getName()
				+ " = \""
				+ tokenName + "\";";
	}
	
	@Override
	protected void prepareVdm(Environment env)
	{
		isConstructor = true;
		name = classDefinition.getName().getName();
		returnType = "";
		body = "\t\t" + f.getName()
				+ " := \""
				+ tokenName + "\";";
	}
}
