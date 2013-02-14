package com.lausdahl.ast.creator.methods.visitors.adaptor.question;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

public class QuestionAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public QuestionAdaptorDefaultTokenMethod()
	{

	}
	

	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}
}

