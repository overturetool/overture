package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.SSetExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SSetExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSetExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(SSetExp expression) {
		switch (expression.kindSSetExp()) {
		case ASetCompSetExp.kindSSetExp:
			return ASetCompSetExpAssistantTC.getOldNames((ASetCompSetExp) expression);
		case ASetEnumSetExp.kindSSetExp:
			return ASetEnumSetExpAssistantTC.getOldNames((ASetEnumSetExp) expression);
		default:
			return new LexNameList();
		}
	}

}
