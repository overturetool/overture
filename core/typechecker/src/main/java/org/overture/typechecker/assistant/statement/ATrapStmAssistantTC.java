package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.ATrapStm;
import org.overture.ast.util.PTypeSet;

public class ATrapStmAssistantTC {

	public static PTypeSet exitCheck(ATrapStm statement) {
		
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistantTC.exitCheck(statement.getBody()));
		types.addAll(PStmAssistantTC.exitCheck(statement.getWith()));
		return types;
	}

}
