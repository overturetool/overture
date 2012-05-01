package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.assistants.PMultipleBindAssistantTC;
import org.overturetool.vdmj.lex.LexNameList;

public class AExistsExpAssistantTC {

	public static LexNameList getOldNameS(AExistsExp expression) {
		
		LexNameList list = new LexNameList();

		for (PMultipleBind mb: expression.getBindList())
		{
			list.addAll(PMultipleBindAssistantTC.getOldNames(mb));
		}

		list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		return list;
	}

}
