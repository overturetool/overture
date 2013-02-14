package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultMethod;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class AnswerAdaptorDefaultMethod extends AnalysisAdaptorDefaultMethod
{
	public AnswerAdaptorDefaultMethod()
	{
		super(null);
	}

	public AnswerAdaptorDefaultMethod(IClassDefinition c)
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
