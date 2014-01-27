package org.overture.typechecker.assistant.type;

import org.overture.ast.assistant.type.ANamedInvariantTypeAssistant;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ANamedInvariantTypeAssistantTC extends
		ANamedInvariantTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANamedInvariantTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

}
