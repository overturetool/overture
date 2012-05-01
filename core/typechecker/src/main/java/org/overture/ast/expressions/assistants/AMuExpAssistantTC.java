package org.overture.ast.expressions.assistants;

import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overturetool.vdmj.lex.LexNameList;

public class AMuExpAssistantTC {

	public static LexNameList getOldNames(AMuExp expression) {
		LexNameList list = PExpAssistantTC.getOldNames(expression.getRecord());

		for (ARecordModifier rm: expression.getModifiers())
		{
			list.addAll(ARecordModifierAssistantTC.getOldNames(rm));
		}

		return list;
	}

}
