package org.overture.ast.patterns.assistants;

import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameList;

public class ATypeMultipleBindAssistantTC {

	public static LexNameList getOldNames(ATypeMultipleBind mb) {
		return new LexNameList();
	}

	public static PType getPossibleType(ATypeMultipleBind mb) {
		return PPatternListAssistantTC.getPossibleType(mb.getPlist(),mb.getLocation());
	}

}
