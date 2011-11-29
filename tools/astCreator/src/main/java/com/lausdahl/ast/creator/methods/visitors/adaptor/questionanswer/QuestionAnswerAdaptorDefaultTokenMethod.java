package com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

public class QuestionAnswerAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public QuestionAnswerAdaptorDefaultTokenMethod()
	{

	}

	public QuestionAnswerAdaptorDefaultTokenMethod(Environment env)
	{
		super(env);
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

