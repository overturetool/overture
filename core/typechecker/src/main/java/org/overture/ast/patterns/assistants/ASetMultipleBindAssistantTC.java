package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.assistants.PExpAssistantTC;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameList;

public class ASetMultipleBindAssistantTC {

	public static PType getPossibleType(ASetMultipleBind mb) {
		return PPatternListAssistantTC.getPossibleType(mb.getPlist(),mb.getLocation());
	}

	public static LexNameList getOldNames(ASetMultipleBind mb) {
		return PExpAssistantTC.getOldNames(mb.getSet());
	}

}
