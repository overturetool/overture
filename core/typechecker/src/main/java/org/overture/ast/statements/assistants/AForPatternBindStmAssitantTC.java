package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AForPatternBindStmAssitantTC {

	public static PTypeSet exitCheck(AForPatternBindStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
