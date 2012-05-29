package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmj.lex.LexNameList;

public class ARecordPatternAssistant {

	

	public static LexNameList getAllVariableNames(ARecordPattern pattern) {
		LexNameList list = new LexNameList();

		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}

		return list;
		
	}

	
	
}
