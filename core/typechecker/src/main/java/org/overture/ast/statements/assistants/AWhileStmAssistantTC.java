package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AWhileStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AWhileStmAssistantTC {

	public static PTypeSet exitCheck(AWhileStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
