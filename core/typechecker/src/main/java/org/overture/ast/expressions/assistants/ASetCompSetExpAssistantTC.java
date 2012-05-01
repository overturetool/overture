package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.assistants.PMultipleBindAssistantTC;
import org.overturetool.vdmj.lex.LexNameList;

public class ASetCompSetExpAssistantTC {

	public static LexNameList getOldNames(ASetCompSetExp expression) {
		
		LexNameList list = PExpAssistantTC.getOldNames(expression.getFirst());

		for (PMultipleBind mb: expression.getBindings())
		{
			list.addAll(PMultipleBindAssistantTC.getOldNames(mb));
		}

		if (expression.getPredicate() != null)
		{
			list.addAll(PExpAssistantTC.getOldNames(expression.getPredicate()));
		}

		return list;
	}

}
