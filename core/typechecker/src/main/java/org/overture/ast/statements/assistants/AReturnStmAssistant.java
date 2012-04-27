package org.overture.ast.statements.assistants;

import org.overture.ast.statements.AReturnStm;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.assistants.PTypeSet;

public class AReturnStmAssistant {
	
	public static PTypeSet exitCheck(AReturnStm statement) {
		if (statement.getExpression() != null)
		{
			// TODO We don't know what an expression will raise
			return new PTypeSet(new AUnknownType(statement.getLocation(),false));
		}
		else
		{
			return PStmAssistant.exitCheckBaseCase(statement);
		}
	}


}
