package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.util.PTypeSet;

public class ALetBeStStmAssistantTC {

	public static PTypeSet exitCheck(ALetBeStStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
