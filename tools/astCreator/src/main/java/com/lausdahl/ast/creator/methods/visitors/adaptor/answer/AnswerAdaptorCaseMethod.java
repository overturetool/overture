package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class AnswerAdaptorCaseMethod extends AnalysisAdaptorCaseMethod
{
	public AnswerAdaptorCaseMethod()
	{
		super(null);
	}

	public AnswerAdaptorCaseMethod(IClassDefinition c)
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
	
}
