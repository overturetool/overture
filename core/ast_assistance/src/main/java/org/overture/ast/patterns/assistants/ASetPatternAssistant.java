package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmj.lex.LexNameList;

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
