package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;

public class QuestionAdaptorDefaultNodeMethod extends
		AnalysisAdaptorDefaultNodeMethod
{
	public QuestionAdaptorDefaultNodeMethod()
	{

	}

	public QuestionAdaptorDefaultNodeMethod(Environment env)
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
