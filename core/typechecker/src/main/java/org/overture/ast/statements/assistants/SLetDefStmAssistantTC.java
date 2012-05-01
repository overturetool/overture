package org.overture.ast.statements.assistants;

import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.types.assistants.PTypeSet;

public class SLetDefStmAssistantTC {

	public static PTypeSet exitCheck(SLetDefStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
