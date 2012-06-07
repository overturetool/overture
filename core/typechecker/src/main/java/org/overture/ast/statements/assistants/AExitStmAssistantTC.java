package org.overture.ast.statements.assistants;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.utils.PTypeSet;

public class AExitStmAssistantTC {

	public static PTypeSet exitCheck(AExitStm statement) {
		
		PTypeSet types = new PTypeSet();

		if (statement.getExpression() == null)
		{
			types.add(AstFactory.newAVoidType(statement.getLocation()));
		}
		else
		{
			types.add(statement.getExpType());
		}

		return types;
	}

	
	
}
