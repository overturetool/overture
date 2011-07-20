package org.overture.ast.statements.assistants;

import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.types.assistants.PTypeSet;

public class SSimpleBlockStmAssistant {

	public static PTypeSet exitCheck(SSimpleBlockStm statement) {
		PTypeSet types = new PTypeSet();

		for (PStm stmt: statement.getStatements())
		{
			types.addAll(PStmAssistant.exitCheck(stmt));
		}

		return types;
	}

}
