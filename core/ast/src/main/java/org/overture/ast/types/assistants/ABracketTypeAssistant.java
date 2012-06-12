package org.overture.ast.types.assistants;

import org.overture.ast.types.ABracketType;
import org.overture.ast.types.SNumericBasicType;

public class ABracketTypeAssistant {

	public static boolean isNumeric(ABracketType type) {
		return PTypeAssistant.isNumeric(type.getType());
	}
	
	public static SNumericBasicType getNumeric(ABracketType type) {
		return PTypeAssistant.getNumeric(type.getType());
	}

	
}
