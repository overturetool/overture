package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.SSeqExp;
import org.overturetool.vdmj.lex.LexNameList;

public class SSeqExpAssistantTC {

	public static LexNameList getOldNames(SSeqExp expression) {
		switch (expression.kindSSeqExp()) {
		case SEQCOMP:
			return ASeqCompSeqExpAssistantTC.getOldNames((ASeqCompSeqExp) expression);
		case SEQENUM:
			return ASeqEnumSeqExpAssistantTC.getOldNames((ASeqEnumSeqExp) expression);
		default:
			assert false : "Should not happen";
			return new LexNameList();
		}
	}

}
