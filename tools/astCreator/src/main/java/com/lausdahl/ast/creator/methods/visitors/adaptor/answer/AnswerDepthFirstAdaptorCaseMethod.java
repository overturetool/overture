package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.analysis.depthfirst.AnalysisDepthFirstAdaptorCaseMethod;

public class AnswerDepthFirstAdaptorCaseMethod extends AnalysisDepthFirstAdaptorCaseMethod
{
	public AnswerDepthFirstAdaptorCaseMethod()
	{
		super(null, null);
	}

	public AnswerDepthFirstAdaptorCaseMethod(IClassDefinition c)
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
}
