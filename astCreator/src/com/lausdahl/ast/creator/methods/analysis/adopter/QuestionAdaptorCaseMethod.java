package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;
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
