package org.overture.ast.statements.assistants;


import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AAlwaysStmAssistant {

	public static PTypeSet exitCheck(AAlwaysStm statement) {
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistant.exitCheck(statement.getBody()));
		types.addAll(PStmAssistant.exitCheck(statement.getAlways()));
		return types;
	}

}
