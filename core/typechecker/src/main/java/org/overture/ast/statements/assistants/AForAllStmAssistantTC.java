package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AForAllStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AForAllStmAssistantTC {

	public static PTypeSet exitCheck(AForAllStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
