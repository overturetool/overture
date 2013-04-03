package org.overture.tools.astcreator.methods.visitors.adaptor.questionanswer;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.analysis.depthfirst.AnalysisDepthFirstAdaptorCaseMethod;

public class QuestionAnswerDepthFirstAdaptorCaseMethod extends
		AnalysisDepthFirstAdaptorCaseMethod
{
	public QuestionAnswerDepthFirstAdaptorCaseMethod()
	{
		super(null, null);
	}

	public QuestionAnswerDepthFirstAdaptorCaseMethod(IClassDefinition c)
	{
		super(c);

	}

	@Override
	protected void prepare(Environment env)
	{
		addReturnToBody = true;
		super.prepare(env);
		this.returnType = "A";
	}

	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}
}
