package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AInMapMapType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AInMapMapTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AInMapMapTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static String toDisplay(AInMapMapType exptype) {
		return "inmap of (" + exptype.getFrom() + ") to (" + exptype.getTo() + ")";
	}

}
