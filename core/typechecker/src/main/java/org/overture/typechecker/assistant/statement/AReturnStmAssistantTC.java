package org.overture.typechecker.assistant.statement;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.util.PTypeSet;

public class AReturnStmAssistantTC {
	
	public static PTypeSet exitCheck(AReturnStm statement) {
		if (statement.getExpression() != null)
		{
			// TODO We don't know what an expression will raise
			return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()));
		}
		else
		{
			return PStmAssistantTC.exitCheckBaseCase(statement);
		}
	}


}
