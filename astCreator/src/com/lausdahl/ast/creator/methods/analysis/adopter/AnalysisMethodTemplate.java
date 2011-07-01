package com.lausdahl.ast.creator.methods.analysis.adopter;

import com.lausdahl.ast.creator.Environment;
import com.lausdahl.ast.creator.definitions.IClassDefinition;
import com.lausdahl.ast.creator.methods.Method;

public abstract class AnalysisMethodTemplate extends Method
{
	protected boolean addReturnToBody = false;

	public AnalysisMethodTemplate(IClassDefinition c, Environment env)
	{
		super(c, env);
	}

	protected String getAdditionalBodyCallArguments()
	{
		String tmp = "";
		for (Argument a : arguments)
		{
			tmp += a.name + ", ";
		}
		if (!arguments.isEmpty())
		{
			tmp = tmp.substring(0, tmp.length() - 2);
		}
		return tmp;
	}

	protected void setupArguments()
	{
		this.arguments.add(new Argument(classDefinition.getName(), "node"));
	}

}
