package org.overture.tools.astcreator.methods.visitors.adaptor.answer;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

public class AnswerAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public AnswerAdaptorDefaultTokenMethod()
	{

	}


	@Override
	protected void prepare(Environment env)
	{
		addReturnToBody = true;
		super.prepare(env);
		this.returnType="A";
	}
}

