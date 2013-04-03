package org.overture.tools.astcreator.methods.visitors.adaptor.question;

import org.overture.tools.astcreator.env.Environment;
import org.overture.tools.astcreator.methods.visitors.adaptor.analysis.AnalysisAdaptorDefaultTokenMethod;

public class QuestionAdaptorDefaultTokenMethod extends 
AnalysisAdaptorDefaultTokenMethod
{
	public QuestionAdaptorDefaultTokenMethod()
	{

	}
	

	@Override
	protected void setupArguments(Environment env)
	{
		super.setupArguments(env);
		this.arguments.add(new Argument("Q", "question"));
	}
}

