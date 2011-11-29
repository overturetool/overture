package com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class QuestionAnswerAdaptorCaseMethod extends AnalysisAdaptorCaseMethod
{
	public QuestionAnswerAdaptorCaseMethod()
	{
		super(null, null);
	}

	public QuestionAnswerAdaptorCaseMethod(IClassDefinition c, Environment env)
	{
		super(c, env);

	}
	
	@Override
	protected void prepare()
	{
		addReturnToBody = true;
		super.prepare();
		this.returnType="A";
	}

	@Override
	protected void setupArguments()
	{
		super.setupArguments();
		this.arguments.add(new Argument("Q", "question"));
	}

}
