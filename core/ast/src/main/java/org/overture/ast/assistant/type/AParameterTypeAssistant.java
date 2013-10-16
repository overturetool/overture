package org.overture.ast.assistant.type;

import org.overture.ast.assistant.IAstAssistantFactory;

public class AParameterTypeAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public AParameterTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

}
