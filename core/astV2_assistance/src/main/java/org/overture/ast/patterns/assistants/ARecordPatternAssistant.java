package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmjV2.lex.LexNameList;

public class ARecordPatternAssistant {

	

	public static LexNameList getVariableNames(ARecordPattern pattern) {
		LexNameList list = new LexNameList();

		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getVariableNames(p));
		}

		return list;
		
	}

	
	
}
