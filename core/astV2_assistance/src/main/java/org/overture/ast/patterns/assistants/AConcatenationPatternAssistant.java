package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AConcatenationPattern;
import org.overturetool.vdmj.lex.LexNameList;

public class AConcatenationPatternAssistant {

	public static LexNameList getVariableNames(AConcatenationPattern pattern) {
		LexNameList list = new LexNameList();

		list.addAll(PPatternAssistant.getVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getVariableNames(pattern.getRight()));

		return list;
	}

}
