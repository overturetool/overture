package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ACaseAlternativeStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACaseAlternativeStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(ACaseAlternativeStm c) {
		return PStmAssistantTC.exitCheck(c.getResult());
	}

}
