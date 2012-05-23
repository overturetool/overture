package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AUnionPattern;
import org.overturetool.vdmj.lex.LexNameList;

public class AUnionPatternAssistant {

	

	public static LexNameList getAllVariableNames(AUnionPattern pattern) {
		LexNameList list = new LexNameList();

		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));

		return list;
	}

	
}
