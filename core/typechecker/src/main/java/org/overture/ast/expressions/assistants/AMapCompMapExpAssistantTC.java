package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.assistants.PMultipleBindAssistantTC;
import org.overturetool.vdmj.lex.LexNameList;

public class AMapCompMapExpAssistantTC {

	public static LexNameList getOldNames(AMapCompMapExp expression) {
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
