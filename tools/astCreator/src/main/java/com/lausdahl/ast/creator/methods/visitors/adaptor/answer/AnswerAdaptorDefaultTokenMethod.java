package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

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

