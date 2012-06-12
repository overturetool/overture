package org.overture.ast.assistant.type;

import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.SNumericBasicType;

public class AOptionalTypeAssistant {

	public static boolean isNumeric(AOptionalType type) {
		return PTypeAssistant.isNumeric(type.getType());
	}
	
	public static SNumericBasicType getNumeric(AOptionalType type) {
		return PTypeAssistant.getNumeric(type.getType());
	}

	
	
}
