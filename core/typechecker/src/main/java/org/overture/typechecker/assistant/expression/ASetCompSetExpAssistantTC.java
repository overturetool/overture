package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class ASetCompSetExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetCompSetExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ASetCompSetExp expression) {
		
		LexNameList list = PExpAssistantTC.getOldNames(expression.getFirst());

		for (PMultipleBind mb: expression.getBindings())
		{
			list.addAll(PMultipleBindAssistantTC.getOldNames(mb));
		}

		if (expression.getPredicate() != null)
		{
			list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		}

		return list;
	}

}
