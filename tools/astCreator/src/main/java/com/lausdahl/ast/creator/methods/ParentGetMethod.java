package com.lausdahl.ast.creator.methods;

import com.lausdahl.ast.creator.definitions.Field;
import com.lausdahl.ast.creator.env.Environment;

public class ParentGetMethod extends Method
{
	Field f;
	String structureClassName;
	public ParentGetMethod(String structureClassName, Field f)
	{
		super(null);
		this.f = f;
		this.structureClassName = structureClassName;
	}
	
	@Override
	protected void prepare(Environment env)
	{
		name = "parent";
		returnType = structureClassName;
		body = "\t\treturn " + f.getName(env) + ";";
	}

}
