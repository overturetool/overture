package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.PType;

public class ARealPatternAssistantTC {

	public static PType getPossibleTypes(ARealPattern pattern) {
		return new ARealNumericBasicType(pattern.getLocation(), false);
	}

}
