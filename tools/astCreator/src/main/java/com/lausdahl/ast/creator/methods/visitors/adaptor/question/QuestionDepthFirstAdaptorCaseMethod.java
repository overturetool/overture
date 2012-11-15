package com.lausdahl.ast.creator.methods.visitors.adaptor.question;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisDepthFirstAdaptorCaseMethod;

public class QuestionDepthFirstAdaptorCaseMethod extends AnalysisDepthFirstAdaptorCaseMethod
{
	public QuestionDepthFirstAdaptorCaseMethod()
	{
		super(null, null);
	}

	public QuestionDepthFirstAdaptorCaseMethod(IClassDefinition c,
			Environment env)
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
