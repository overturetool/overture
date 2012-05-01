package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.SNumericBasicTypeAssistant;

public class AIntegerPatternAssistantTC {

	public static PType getPossibleTypes(AIntegerPattern pattern) {
		return SNumericBasicTypeAssistant.typeOf(pattern.getValue().value, pattern.getLocation());
	}

}
