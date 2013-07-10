package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASetEnumSetExpAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetEnumSetExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ASetEnumSetExp expression) {
		return PExpAssistantTC.getOldNames(expression.getMembers());
	}

}
