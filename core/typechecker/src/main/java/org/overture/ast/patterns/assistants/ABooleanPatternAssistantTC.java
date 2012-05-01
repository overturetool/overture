package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;

public class ABooleanPatternAssistantTC {

	public static PType getPossibleType(ABooleanPattern pattern) {
		return new ABooleanBasicType(pattern.getLocation(), false);
	}

}
