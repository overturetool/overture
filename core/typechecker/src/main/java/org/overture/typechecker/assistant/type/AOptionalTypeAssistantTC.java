package org.overture.typechecker.assistant.type;

import org.overture.ast.assistant.type.AOptionalTypeAssistant;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AOptionalTypeAssistantTC extends AOptionalTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AOptionalTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

}
