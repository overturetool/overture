package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.types.assistants.PTypeSet;

public class AIfStmAssistant {

	public static PTypeSet exitCheck(AIfStm statement) {
		
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistant.exitCheck(statement.getThenStm()));

		for (AElseIfStm stmt: statement.getElseIf())
		{
			types.addAll(AElseIfStmAssistant.exitCheck(stmt));
		}

		if (statement.getElseStm() != null)
		{
			types.addAll(PStmAssistant.exitCheck(statement.getElseStm()));
		}

		return types;
	}

}
