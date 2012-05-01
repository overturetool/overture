package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASameBaseClassExp;
import org.overturetool.vdmj.lex.LexNameList;

public class ASameBaseClassExpAssistantTC {

	public static LexNameList getOldNames(ASameBaseClassExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getLeft());
		list.addAll(PExpAssistantTC.getOldNames(expression.getRight()));
		return list;
	}

}
