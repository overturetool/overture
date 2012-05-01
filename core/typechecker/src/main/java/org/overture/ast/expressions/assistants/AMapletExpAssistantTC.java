package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AMapletExp;
import org.overturetool.vdmj.lex.LexNameList;

public class AMapletExpAssistantTC {

	public static LexNameList getOldNames(AMapletExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getLeft());
		list.addAll(PExpAssistantTC.getOldNames(expression.getRight()));
		return list;
	}

}
