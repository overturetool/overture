package org.overture.ast.statements.assistants;

import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.types.assistants.PTypeSet;


public class ACasesStmAssistant {

	public static PTypeSet exitCheck(ACasesStm statement) {
		PTypeSet types = new PTypeSet();

		for (ACaseAlternativeStm c: statement.getCases())
		{
			//TODO: MISSING CASE ALTERNATIVE
			//types.addAll(ACaseAlternativeStmAssistant.exitCheck(c));
		}

		return types;
	}

}
