package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class AnswerAdaptorCaseMethod extends AnalysisAdaptorCaseMethod
{
	public AnswerAdaptorCaseMethod()
	{
		super(null,null);
	}

	public AnswerAdaptorCaseMethod(IClassDefinition c,Environment env)
	{
		super(c,env);
		
	}
	

	@Override
	protected void prepare()
	{
		addReturnToBody = true;
		super.prepare();
		this.returnType="A";
	}
	
}
