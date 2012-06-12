package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.util.PTypeSet;

public class AElseIfStmAssistantTC {

	public static PTypeSet exitCheck(AElseIfStm statement) {
		return PStmAssistantTC.exitCheck(statement.getThenStm());
	}

}
