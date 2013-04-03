package org.overture.tools.astcreator.methods.visitors.adaptor.question;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.analysis.depthfirst.AnalysisDepthFirstAdaptorCaseMethod;

public class QuestionDepthFirstAdaptorCaseMethod extends AnalysisDepthFirstAdaptorCaseMethod
{
	public QuestionDepthFirstAdaptorCaseMethod()
	{
		super(null, null);
	}

	public QuestionDepthFirstAdaptorCaseMethod(IClassDefinition c)
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
