package org.overture.tools.astcreator.definitions;

import org.overture.tools.astcreator.env.Environment;

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
