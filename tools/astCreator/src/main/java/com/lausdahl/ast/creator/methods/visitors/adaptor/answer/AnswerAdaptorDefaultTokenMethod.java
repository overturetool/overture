package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

public class AnswerAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public AnswerAdaptorDefaultTokenMethod()
	{

	}


	@Override
	protected void prepare(Environment env)
	{
		addReturnToBody = true;
		super.prepare(env);
		this.returnType="A";
	}
}

