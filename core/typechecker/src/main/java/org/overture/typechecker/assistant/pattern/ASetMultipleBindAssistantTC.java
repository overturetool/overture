package org.overture.typechecker.assistant.pattern;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;

public class ASetMultipleBindAssistantTC {

	public static PType getPossibleType(ASetMultipleBind mb) {
		return PPatternListAssistantTC.getPossibleType(mb.getPlist(),mb.getLocation());
	}

	public static LexNameList getOldNames(ASetMultipleBind mb) {
		return PExpAssistantTC.getOldNames(mb.getSet());
	}

}
