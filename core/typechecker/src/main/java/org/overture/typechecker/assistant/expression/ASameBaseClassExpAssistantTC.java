package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.lex.LexNameList;

public class ASameBaseClassExpAssistantTC {

	public static LexNameList getOldNames(ASameBaseClassExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getLeft());
		list.addAll(PExpAssistantTC.getOldNames(expression.getRight()));
		return list;
	}

}
