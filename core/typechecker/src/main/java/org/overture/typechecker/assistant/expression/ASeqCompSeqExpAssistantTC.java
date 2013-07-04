package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.ASetBindAssistantTC;

public class ASeqCompSeqExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASeqCompSeqExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ASeqCompSeqExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getFirst());
		list.addAll(ASetBindAssistantTC.getOldNames(expression.getSetBind()));

		if (expression.getPredicate() != null) {
			list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		}

		return list;
	}

}
