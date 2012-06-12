package org.overture.typechecker.assistant.pattern;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.types.PType;

public class ATypeMultipleBindAssistantTC {

	public static LexNameList getOldNames(ATypeMultipleBind mb) {
		return new LexNameList();
	}

	public static PType getPossibleType(ATypeMultipleBind mb) {
		return PPatternListAssistantTC.getPossibleType(mb.getPlist(),mb.getLocation());
	}

}
