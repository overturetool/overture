package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.PType;

public class AQuotePatternAssistantTC {

	public static PType getPossibleTypes(AQuotePattern pattern) {
		return new AQuoteType(pattern.getLocation(), false, ((AQuotePattern) pattern).getValue().clone());
	}

}
