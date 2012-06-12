package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.util.PTypeSet;

public class SLetDefStmAssistantTC {

	public static PTypeSet exitCheck(SLetDefStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
