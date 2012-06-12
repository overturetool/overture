package org.overture.typechecker.assistant.expression;

import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.lex.LexNameList;

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
