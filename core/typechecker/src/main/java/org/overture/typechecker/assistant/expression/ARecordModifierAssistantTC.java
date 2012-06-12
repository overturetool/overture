package org.overture.typechecker.assistant.expression;


import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.lex.LexNameList;

public class ARecordModifierAssistantTC {

	public static LexNameList getOldNames(
			ARecordModifier rm) {
		return PExpAssistantTC.getOldNames(rm.getValue());
	}

}
