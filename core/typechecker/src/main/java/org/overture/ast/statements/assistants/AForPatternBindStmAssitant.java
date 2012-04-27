package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AForPatternBindStmAssitant {

	public static PTypeSet exitCheck(AForPatternBindStm statement) {
		return PStmAssistant.exitCheck(statement.getStatement());
	}

}
