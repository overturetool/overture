package com.lausdahl.ast.creator.definitions;

import com.lausdahl.ast.creator.Environment;

public class RefinedField extends Field
{
	Field sourceField = null;

	public RefinedField(Environment env, Field sourceField)
	{
		super(env);
		this.sourceField = sourceField;
	}

}
