package com.lausdahl.ast.creator.methods.visitors.adaptor.question;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class QuestionAdaptorCaseMethod extends AnalysisAdaptorCaseMethod
{
	public QuestionAdaptorCaseMethod()
	{
		super(null);
	}

	public QuestionAdaptorCaseMethod(IClassDefinition c)
	{
		super(c);

	}

	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}

}
