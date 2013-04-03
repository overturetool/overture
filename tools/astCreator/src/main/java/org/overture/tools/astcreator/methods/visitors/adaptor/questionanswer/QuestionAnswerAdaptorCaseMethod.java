package org.overture.tools.astcreator.methods.visitors.adaptor.questionanswer;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import org.overture.tools.astcreator.definitions.IClassDefinition;

public class QuestionAnswerAdaptorCaseMethod extends AnalysisAdaptorCaseMethod
{
	public QuestionAnswerAdaptorCaseMethod()
	{
		super(null);
	}

	public QuestionAnswerAdaptorCaseMethod(IClassDefinition c)
	{
		super(c);

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
