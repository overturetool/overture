package org.overture.codegen.assistant;

public abstract class AssistantBase
{
	protected AssistantManager assistantManager;

	public AssistantBase(AssistantManager assistantManager)
	{
		super();
		this.assistantManager = assistantManager != null ? assistantManager : new AssistantManager();
	}
}
