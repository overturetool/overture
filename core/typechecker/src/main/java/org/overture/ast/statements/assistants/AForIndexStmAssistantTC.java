package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.utils.PTypeSet;

public class AForIndexStmAssistantTC {

	public static PTypeSet exitCheck(AForIndexStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
