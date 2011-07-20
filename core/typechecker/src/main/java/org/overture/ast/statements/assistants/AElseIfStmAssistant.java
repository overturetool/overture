package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AElseIfStmAssistant {

	public static PTypeSet exitCheck(AElseIfStm statement) {
		return PStmAssistant.exitCheck(statement.getThenStm());
	}

}
