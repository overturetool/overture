package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AElseIfStmAssistantTC {

	public static PTypeSet exitCheck(AElseIfStm statement) {
		return PStmAssistantTC.exitCheck(statement.getThenStm());
	}

}
