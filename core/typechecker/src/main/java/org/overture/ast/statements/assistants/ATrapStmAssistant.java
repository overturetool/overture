package org.overture.ast.statements.assistants;

import org.overture.ast.statements.ATrapStm;
import org.overture.ast.types.assistants.PTypeSet;

public class ATrapStmAssistant {

	public static PTypeSet exitCheck(ATrapStm statement) {
		
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistant.exitCheck(statement.getBody()));
		types.addAll(PStmAssistant.exitCheck(statement.getWith()));
		return types;
	}

}
