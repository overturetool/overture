package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.PType;

public class ACharacterPatternAssistantTC {

	public static PType getPossibleType(ACharacterPattern pattern) {
		return new ACharBasicType(pattern.getLocation(), false);
	}

}
