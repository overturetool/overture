package org.overture.tools.astcreator.methods.visitors.adaptor.answer;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import org.overture.tools.astcreator.definitions.IClassDefinition;

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
