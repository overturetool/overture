package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AExitStm;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.assistants.PTypeSet;

public class AExitStmAssistant {

	public static PTypeSet exitCheck(AExitStm statement) {
		
		PTypeSet types = new PTypeSet();

		if (statement.getExpression() == null)
		{
			types.add(new AVoidType(statement.getLocation(),false));
		}
		else
		{
			types.add(statement.getExpType());
		}

		return types;
	}

	
	
}
