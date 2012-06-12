package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AWhileStm;
import org.overture.ast.util.PTypeSet;

public class AWhileStmAssistantTC {

	public static PTypeSet exitCheck(AWhileStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
