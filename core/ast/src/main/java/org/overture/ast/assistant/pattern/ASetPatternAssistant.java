package org.overture.ast.assistant.pattern;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.PPattern;

public class ASetPatternAssistant {



	public static LexNameList getAllVariableNames(ASetPattern pattern) {
		LexNameList list = new LexNameList();

		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}

		return list;
	}



}
