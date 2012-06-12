package org.overture.ast.patterns.assistants;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.AUnionPattern;

public class AUnionPatternAssistant {

	

	public static LexNameList getAllVariableNames(AUnionPattern pattern) {
		LexNameList list = new LexNameList();

		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));

		return list;
	}

	
}
