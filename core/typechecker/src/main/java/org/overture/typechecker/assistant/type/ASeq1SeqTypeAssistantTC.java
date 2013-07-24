package org.overture.typechecker.assistant.type;

import org.overture.ast.types.ASeq1SeqType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASeq1SeqTypeAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASeq1SeqTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static String toDisplay(ASeq1SeqType exptype) {
		return exptype.getEmpty() ? "[]" : "seq1 of (" + exptype.getSeqof() + ")";
	}

}
