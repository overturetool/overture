package com.lausdahl.ast.creator.definitions;

import com.lausdahl.ast.creator.env.Environment;

public class ExternalEnumJavaClassDefinition extends
		ExternalJavaClassDefinition
{
	public ExternalEnumJavaClassDefinition(String rawName,
			IClassDefinition superClass, ClassType type, String name,
			Environment env)
	{
		super(rawName, superClass, type, name, false,env);
		
	}
}
