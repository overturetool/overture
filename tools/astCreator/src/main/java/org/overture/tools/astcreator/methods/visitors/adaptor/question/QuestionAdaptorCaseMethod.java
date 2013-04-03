package org.overture.tools.astcreator.methods.visitors.adaptor.question;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorCaseMethod;
import org.overture.tools.astcreator.definitions.IClassDefinition;

public class QuestionAdaptorCaseMethod extends AnalysisAdaptorCaseMethod
{
	public QuestionAdaptorCaseMethod()
	{
		super(null);
	}

	public QuestionAdaptorCaseMethod(IClassDefinition c)
	{
		super(c);

	}

	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}

}
