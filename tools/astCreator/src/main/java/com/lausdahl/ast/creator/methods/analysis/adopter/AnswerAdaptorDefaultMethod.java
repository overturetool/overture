package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class AnswerAdaptorDefaultMethod extends AnalysisAdaptorDefaultMethod
{
	public AnswerAdaptorDefaultMethod()
	{
		super(null, null);
	}

	public AnswerAdaptorDefaultMethod(IClassDefinition c, Environment env)
	{
		super(c, env);
	}

	@Override
	protected void prepare()
	{
		addReturnToBody = true;
		super.prepare();
		this.returnType="A";
	}
}
