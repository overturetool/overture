package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.util.PTypeSet;

public class ACaseAlternativeStmAssistantTC {

	public static PTypeSet exitCheck(ACaseAlternativeStm c) {
		return PStmAssistantTC.exitCheck(c.getResult());
	}

}
