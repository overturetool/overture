package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AUnionPattern;
import org.overturetool.vdmj.lex.LexNameList;

public class AUnionPatternAssistant {

	

	public static LexNameList getVariableNames(AUnionPattern pattern) {
		LexNameList list = new LexNameList();

		list.addAll(PPatternAssistant.getVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getVariableNames(pattern.getRight()));

		return list;
	}

	
}
