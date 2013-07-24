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
		switch (expression.kindSSeqExp()) {
		case ASeqCompSeqExp.kindSSeqExp:
			return ASeqCompSeqExpAssistantTC.getOldNames((ASeqCompSeqExp) expression);
		case ASeqEnumSeqExp.kindSSeqExp:
			return ASeqEnumSeqExpAssistantTC.getOldNames((ASeqEnumSeqExp) expression);
		default:
			assert false : "Should not happen";
			return new LexNameList();
		}
	}

}
