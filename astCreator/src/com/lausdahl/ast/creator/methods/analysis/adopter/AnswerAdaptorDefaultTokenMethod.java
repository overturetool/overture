package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;

public class AnswerAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public AnswerAdaptorDefaultTokenMethod()
	{

	}

	public AnswerAdaptorDefaultTokenMethod(Environment env)
	{
		super(env);
	}

	@Override
	protected void prepare()
	{
		addReturnToBody = true;
		super.prepare();
		this.returnType="A";
	}
}

