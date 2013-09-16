package org.overture.typechecker.assistant.statement;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AReturnStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AReturnStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
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
