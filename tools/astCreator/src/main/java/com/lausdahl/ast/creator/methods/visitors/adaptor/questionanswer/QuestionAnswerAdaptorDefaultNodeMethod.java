package com.lausdahl.ast.creator.methods.visitors.adaptor.questionanswer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultNodeMethod;

public class QuestionAnswerAdaptorDefaultNodeMethod extends
		AnalysisAdaptorDefaultNodeMethod
{
	public QuestionAnswerAdaptorDefaultNodeMethod()
	{

	}

	public QuestionAnswerAdaptorDefaultNodeMethod(Environment env)
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
