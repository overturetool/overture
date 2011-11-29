package com.lausdahl.ast.creator.methods.visitors.adaptor.question;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

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

