package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisDepthFirstAdaptorCaseMethod;

public class AnswerDepthFirstAdaptorCaseMethod extends AnalysisDepthFirstAdaptorCaseMethod
{
	public AnswerDepthFirstAdaptorCaseMethod()
	{
		super(null, null);
	}

	public AnswerDepthFirstAdaptorCaseMethod(IClassDefinition c, Environment env)
	{
		super(c, env);

	}

	@Override
	protected void prepare()
	{
		addReturnToBody = true;
		super.prepare();
		this.returnType = "A";
	}
}
