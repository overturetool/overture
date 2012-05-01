package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;

public class AIgnorePatternAssistantTC {

	public static PType getPossibleTypes(AIgnorePattern pattern) {
		return new AUnknownType(pattern.getLocation(), false);
	}

}
