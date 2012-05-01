package org.overture.ast.expressions.assistants;


import org.overture.ast.expressions.ARecordModifier;
import org.overturetool.vdmj.lex.LexNameList;

public class ARecordModifierAssistantTC {

	public static LexNameList getOldNames(
			ARecordModifier rm) {
		return PExpAssistantTC.getOldNames(rm.getValue());
	}

}
