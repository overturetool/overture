package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.ast.lex.LexNameList;

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
