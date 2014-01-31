package org.overture.typechecker.assistant.type;

import org.overture.ast.assistant.type.AParameterTypeAssistant;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AParameterTypeAssistantTC extends AParameterTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AParameterTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

}
