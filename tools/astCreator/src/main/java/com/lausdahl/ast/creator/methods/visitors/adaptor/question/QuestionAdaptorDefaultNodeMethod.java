package com.lausdahl.ast.creator.methods.visitors.adaptor.question;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultNodeMethod;

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
