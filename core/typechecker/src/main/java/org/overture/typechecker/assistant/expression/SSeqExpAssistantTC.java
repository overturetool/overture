package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SSeqExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSeqExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(SSeqExp expression) {
		if (expression instanceof ASeqCompSeqExp) {
			return ASeqCompSeqExpAssistantTC.getOldNames((ASeqCompSeqExp) expression);
		} else if (expression instanceof ASeqEnumSeqExp) {
			return ASeqEnumSeqExpAssistantTC.getOldNames((ASeqEnumSeqExp) expression);
		} else {
			assert false : "Should not happen";
			return new LexNameList();
		}
	}

}
