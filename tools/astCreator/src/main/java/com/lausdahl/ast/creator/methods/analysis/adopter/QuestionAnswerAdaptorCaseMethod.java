package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;
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
