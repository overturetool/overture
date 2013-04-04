package org.overture.tools.astcreator.methods.visitors.adaptor.answer;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultNodeMethod;

public class AnswerAdaptorDefaultNodeMethod extends
		AnalysisAdaptorDefaultNodeMethod
{
	public AnswerAdaptorDefaultNodeMethod()
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
