package org.overture.tools.astcreator.methods.visitors.adaptor.answer;

import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.analysis.depthfirst.AnalysisDepthFirstAdaptorCaseMethod;

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
