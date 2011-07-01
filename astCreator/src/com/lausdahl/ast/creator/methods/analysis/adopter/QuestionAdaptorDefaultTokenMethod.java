package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;

public class QuestionAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public QuestionAdaptorDefaultTokenMethod()
	{

	}

	public QuestionAdaptorDefaultTokenMethod(Environment env)
	{
		super(env);
	}

	@Override
	protected void setupArguments()
	{
		super.setupArguments();
		this.arguments.add(new Argument("Q", "question"));
	}
}

