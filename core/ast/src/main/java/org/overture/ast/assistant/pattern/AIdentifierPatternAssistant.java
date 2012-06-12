package org.overture.ast.assistant.pattern;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.AIdentifierPattern;

public class AIdentifierPatternAssistant {

	public static LexNameList getAllVariableNames(AIdentifierPattern pattern) {
		LexNameList list = new LexNameList();
		list.add(pattern.getName()); 
		return list;
	}

}
