package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASubseqExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASubseqExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ASubseqExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getSeq());
		list.addAll(PExpAssistantTC.getOldNames(expression.getFrom()));
		list.addAll(PExpAssistantTC.getOldNames(expression.getTo()));
		return list;
	}

}
