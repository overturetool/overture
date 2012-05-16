package org.overture.ast.statements.assistants;

import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.types.assistants.PTypeSet;

public class ACaseAlternativeStmAssistantTC {

	public static PTypeSet exitCheck(ACaseAlternativeStm c) {
		return PStmAssistantTC.exitCheck(c.getResult());
	}

}
