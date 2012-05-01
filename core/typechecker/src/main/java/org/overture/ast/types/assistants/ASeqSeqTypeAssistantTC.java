package org.overture.ast.types.assistants;

import org.overture.ast.types.ASeqSeqType;

public class ASeqSeqTypeAssistantTC {

	public static String toDisplay(ASeqSeqType exptype) {
		return exptype.getEmpty() ? "[]" : "seq of (" + exptype.getSeqof() + ")";
	}

}
