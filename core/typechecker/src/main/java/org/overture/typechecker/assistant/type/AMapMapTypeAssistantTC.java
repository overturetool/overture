package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AMapMapType;

public class AMapMapTypeAssistantTC {

	public static String toDisplay(AMapMapType exptype) {
		return "map (" + exptype.getFrom() + ") to (" + exptype.getTo() + ")";
	}

}
