package org.overture.codegen.assistant;

import org.overture.codegen.cgast.patterns.ASetBindCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;

public class BindAssistantCG extends AssistantBase
{
	public BindAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public ASetMultipleBindCG convertToMultipleSetBind(ASetBindCG setBind)
	{
		ASetMultipleBindCG multipleSetBind = new ASetMultipleBindCG();
		
		multipleSetBind.getPatterns().add(setBind.getPattern());
		multipleSetBind.setSet(setBind.getSet());
		
		return multipleSetBind;
	}
}
