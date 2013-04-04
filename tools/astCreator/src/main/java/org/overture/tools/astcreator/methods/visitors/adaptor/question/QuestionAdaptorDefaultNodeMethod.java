package org.overture.tools.astcreator.methods.visitors.adaptor.question;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultNodeMethod;

public class QuestionAdaptorDefaultNodeMethod extends
		AnalysisAdaptorDefaultNodeMethod
{
	public QuestionAdaptorDefaultNodeMethod()
	{

	}


	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}
}
