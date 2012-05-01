package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;

public class AStringPatternAssistantTC {

	public static PType getPossibleTypes(AStringPattern pattern) {
		ASeqSeqType t = new ASeqSeqType(pattern.getLocation(), false, false);
		t.setSeqof( new AUnknownType(pattern.getLocation(), false));
		return t;
	}

}
