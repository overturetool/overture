package org.overture.ast.statements.assistants;

import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.utils.PTypeSet;

public class SSimpleBlockStmAssistantTC {

	public static PTypeSet exitCheck(SSimpleBlockStm statement) {
		PTypeSet types = new PTypeSet();

		for (PStm stmt: statement.getStatements())
		{
			types.addAll(PStmAssistantTC.exitCheck(stmt));
		}

		return types;
	}

}
