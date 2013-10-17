package org.overture.typechecker.assistant.type;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMapMapTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapMapTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
//	public static String toDisplay(AMapMapType exptype) {
//		return "map (" + exptype.getFrom() + ") to (" + exptype.getTo() + ")";
//	}

}
