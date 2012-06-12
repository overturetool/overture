package org.overture.ast.patterns.assistants;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.AConcatenationPattern;

public class AConcatenationPatternAssistant {

	public static LexNameList getAllVariableNames(AConcatenationPattern pattern) {
		LexNameList list = new LexNameList();

		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));

		return list;
	}

}
