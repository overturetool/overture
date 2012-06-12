package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.SSetExp;
import org.overture.ast.lex.LexNameList;

public class SSetExpAssistantTC {

	public static LexNameList getOldNames(SSetExp expression) {
		switch (expression.kindSSetExp()) {
		case SETCOMP:
			return ASetCompSetExpAssistantTC.getOldNames((ASetCompSetExp) expression);
		case SETENUM:
			return ASetEnumSetExpAssistantTC.getOldNames((ASetEnumSetExp) expression);
		default:
			return new LexNameList();
		}
	}

}
