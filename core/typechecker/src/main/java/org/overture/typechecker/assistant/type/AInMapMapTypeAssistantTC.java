package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AInMapMapType;

public class AInMapMapTypeAssistantTC {

	public static String toDisplay(AInMapMapType exptype) {
		return "inmap of (" + exptype.getFrom() + ") to (" + exptype.getTo() + ")";
	}

}
