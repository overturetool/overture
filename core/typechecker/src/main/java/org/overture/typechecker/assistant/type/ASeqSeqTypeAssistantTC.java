package org.overture.typechecker.assistant.type;

import org.overture.ast.types.ASeqSeqType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASeqSeqTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASeqSeqTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static String toDisplay(ASeqSeqType exptype) {
		return exptype.getEmpty() ? "[]" : "seq of (" + exptype.getSeqof() + ")";
	}

}
