package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASeqEnumSeqExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASeqEnumSeqExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ASeqEnumSeqExp expression) {
		return PExpAssistantTC.getOldNames(expression.getMembers());
	}

}
