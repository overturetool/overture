package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class TokenConstructorMethod extends Method
{

	private String tokenName;
	private Field f;


	public TokenConstructorMethod(IClassDefinition c,Field f, String tokenName,Environment env)
	{
		super(c,env);
		this.f = f;
		this.tokenName = tokenName;
	}

	
	@Override
	protected void prepare()
	{
		isConstructor = true;
		name = classDefinition.getName();
		returnType = "";
		body = "\t\t" + f.getName()
				+ " = \""
				+ tokenName + "\";";
	}
	
	@Override
	protected void prepareVdm()
	{
		isConstructor = true;
		name = classDefinition.getName();
		returnType = "";
		body = "\t\t" + f.getName()
				+ " := \""
				+ tokenName + "\";";
	}
}
