package org.overture.codegen.assistant;

import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;

public class PatternAssistantCG  extends AssistantBase
{
	public PatternAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public AIdentifierPatternCG consIdPattern(String name)
	{
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(name);

		return idPattern;
	}
}
