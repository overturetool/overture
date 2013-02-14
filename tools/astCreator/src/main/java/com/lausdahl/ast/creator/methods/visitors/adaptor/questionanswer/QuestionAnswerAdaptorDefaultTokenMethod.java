package com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

public class QuestionAnswerAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public QuestionAnswerAdaptorDefaultTokenMethod()
	{

	}

	
	@Override
	protected void prepare(Environment env)
	{
		addReturnToBody = true;
		super.prepare(env);
		this.returnType="A";
	}

	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}
}

