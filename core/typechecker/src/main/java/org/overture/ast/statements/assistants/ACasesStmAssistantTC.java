package org.overture.ast.statements.assistants;

import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.types.assistants.PTypeSet;


public class ACasesStmAssistantTC {

	public static PTypeSet exitCheck(ACasesStm statement) {
		PTypeSet types = new PTypeSet();

		for (ACaseAlternativeStm c: statement.getCases())
		{
			types.addAll(ACaseAlternativeStmAssistantTC.exitCheck(c));
		}

		return types;
	}

}
