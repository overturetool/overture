package org.overture.tools.astcreator.methods.visitors.adaptor.question;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultMethod;
import org.overture.tools.astcreator.definitions.IClassDefinition;

public class QuestionAdaptorDefaultMethod extends AnalysisAdaptorDefaultMethod
{
	public QuestionAdaptorDefaultMethod()
	{
		super(null);
	}

	public QuestionAdaptorDefaultMethod(IClassDefinition c)
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
