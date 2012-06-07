package org.overture.ast.statements.assistants;


import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.utils.PTypeSet;

public class AAlwaysStmAssistantTC {

	public static PTypeSet exitCheck(AAlwaysStm statement) {
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistantTC.exitCheck(statement.getBody()));
		types.addAll(PStmAssistantTC.exitCheck(statement.getAlways()));
		return types;
	}

}
