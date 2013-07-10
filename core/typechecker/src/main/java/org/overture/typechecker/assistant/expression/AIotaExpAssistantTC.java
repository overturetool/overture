package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;

public class AIotaExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIotaExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(AIotaExp expression) {
		LexNameList list = PBindAssistantTC.getOldNames(expression.getBind());
		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
