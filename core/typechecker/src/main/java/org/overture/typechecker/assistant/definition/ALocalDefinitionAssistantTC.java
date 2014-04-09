package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ALocalDefinitionAssistantTC
{

	protected ITypeCheckerAssistantFactory af;

	public ALocalDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public void setValueDefinition(ALocalDefinition ld)
	{
		ld.setValueDefinition(true);

	}

}
