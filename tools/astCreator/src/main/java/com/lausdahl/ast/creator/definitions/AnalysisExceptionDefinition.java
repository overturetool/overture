package com.lausdahl.ast.creator.definitions;

import com.lausdahl.ast.creator.java.definitions.JavaName;

public class AnalysisExceptionDefinition extends BaseClassDefinition
{

	public AnalysisExceptionDefinition(JavaName name)
	{
		super(name);
		superDef = new PredefinedClassDefinition("java.lang", "Exception");
	}

	public AnalysisExceptionDefinition(String packageName, String name)
	{
		this(new JavaName(packageName, name));
	}

}
