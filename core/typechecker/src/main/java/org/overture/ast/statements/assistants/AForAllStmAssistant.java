package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AForAllStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AForAllStmAssistant {

	public static PTypeSet exitCheck(AForAllStm statement) {
		return PStmAssistant.exitCheck(statement.getStatement());
	}

}
