package org.overture.tools.astcreator.methods.visitors.adaptor.answer;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultMethod;
import org.overture.tools.astcreator.definitions.IClassDefinition;

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
