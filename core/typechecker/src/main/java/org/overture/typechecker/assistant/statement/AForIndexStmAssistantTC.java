package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.util.PTypeSet;

public class AForIndexStmAssistantTC {

	public static PTypeSet exitCheck(AForIndexStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
