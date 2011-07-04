package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class QuestionAdaptorDefaultMethod extends AnalysisAdaptorDefaultMethod
{
	public QuestionAdaptorDefaultMethod()
	{
		super(null, null);
	}

	public QuestionAdaptorDefaultMethod(IClassDefinition c, Environment env)
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
