package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AWhileStm;
import org.overture.ast.utils.PTypeSet;

public class AWhileStmAssistantTC {

	public static PTypeSet exitCheck(AWhileStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
