package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.AIdentifierPattern;
import org.overturetool.vdmj.lex.LexNameList;

public class AIdentifierPatternAssistant {

	public static LexNameList getVariableNames(AIdentifierPattern pattern) {
		LexNameList list = new LexNameList();
		list.add(pattern.getName());
		return list;
	}

}
