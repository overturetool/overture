package com.lausdahl.ast.creator.methods.visitors.adaptor.question;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class QuestionAdaptorCaseMethod extends AnalysisAdaptorCaseMethod
{
	public QuestionAdaptorCaseMethod()
	{
		super(null, null);
	}

	public QuestionAdaptorCaseMethod(IClassDefinition c, Environment env)
	{
		super(c, env);

	}

	@Override
	protected void setupArguments()
	{
		super.setupArguments();
		this.arguments.add(new Argument("Q", "question"));
	}

}
