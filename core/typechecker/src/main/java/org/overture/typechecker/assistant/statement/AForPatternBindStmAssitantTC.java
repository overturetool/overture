package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.util.PTypeSet;

public class AForPatternBindStmAssitantTC {

	public static PTypeSet exitCheck(AForPatternBindStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
