package com.lausdahl.ast.creator.methods.visitors.adaptor.answer;

import com.lausdahl.ast.creator.env.Environment;
import com.lausdahl.ast.creator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultMethod;
import com.lausdahl.ast.creator.definitions.IClassDefinition;

public class AnswerAdaptorDefaultMethod extends AnalysisAdaptorDefaultMethod
{
	public AnswerAdaptorDefaultMethod()
	{
		super(null, null);
	}

	public AnswerAdaptorDefaultMethod(IClassDefinition c, Environment env)
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
}
