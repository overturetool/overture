package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;

public class ANilPatternAssistantTC {

	public static PType getPossibleTypes(ANilPattern pattern) {
		return new AOptionalType(pattern.getLocation(), false,null, new AUnknownType(pattern.getLocation(), false));
	}

}
