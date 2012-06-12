package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AForAllStm;
import org.overture.ast.util.PTypeSet;

public class AForAllStmAssistantTC {

	public static PTypeSet exitCheck(AForAllStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
