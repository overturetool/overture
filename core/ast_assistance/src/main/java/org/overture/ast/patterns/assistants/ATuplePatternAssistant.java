package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmj.lex.LexNameList;

public class ATuplePatternAssistant {

	
	public static LexNameList getAllVariableNames(ATuplePattern pattern) {
		LexNameList list = new LexNameList();

		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}

		return list;
	}

	
}
