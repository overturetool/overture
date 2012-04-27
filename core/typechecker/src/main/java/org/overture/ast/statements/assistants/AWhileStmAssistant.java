package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AWhileStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AWhileStmAssistant {

	public static PTypeSet exitCheck(AWhileStm statement) {
		return PStmAssistant.exitCheck(statement.getStatement());
	}

}
