package org.overture.tools.astcreator.methods.visitors.adaptor.questionanswer;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultNodeMethod;

public class QuestionAnswerAdaptorDefaultNodeMethod extends
		AnalysisAdaptorDefaultNodeMethod
{
	public QuestionAnswerAdaptorDefaultNodeMethod()
	{

	}

	
	@Override
	protected void prepare(Environment env)
	{
		addReturnToBody = true;
		super.prepare(env);
		this.returnType="A";
	}

	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}
}
