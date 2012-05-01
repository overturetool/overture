package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASameClassExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ASameClassExpAssistantTC {

	public static LexNameList getOldNames(ASameClassExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getLeft());
		list.addAll(PExpAssistantTC.getOldNames(expression.getRight()));
		return list;
	}

}
