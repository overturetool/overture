package org.overture.ast.types.assistants;

import org.overture.ast.types.ASeqSeqType;

public class ASeqSeqTypeAssistant {

	public static String toDisplay(ASeqSeqType exptype) {
		return exptype.getEmpty() ? "[]" : "seq of (" + exptype.getSeqof() + ")";
	}

}
