package org.overture.typechecker.assistant.pattern;

import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ATypeMultipleBindAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATypeMultipleBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static LexNameList getOldNames(ATypeMultipleBind mb) {
		return new LexNameList();
	}

	public static PType getPossibleType(ATypeMultipleBind mb) {
		return PPatternListAssistantTC.getPossibleType(mb.getPlist(),mb.getLocation());
	}

}
