package org.overture.typechecker.assistant.statement;


import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.util.PTypeSet;

public class AAlwaysStmAssistantTC {

	public static PTypeSet exitCheck(AAlwaysStm statement) {
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistantTC.exitCheck(statement.getBody()));
		types.addAll(PStmAssistantTC.exitCheck(statement.getAlways()));
		return types;
	}

}
