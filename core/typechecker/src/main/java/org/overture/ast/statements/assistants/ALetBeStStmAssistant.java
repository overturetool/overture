package org.overture.ast.statements.assistants;

import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.types.assistants.PTypeSet;

public class ALetBeStStmAssistant {

	public static PTypeSet exitCheck(ALetBeStStm statement) {
		return PStmAssistant.exitCheck(statement.getStatement());
	}

}
