package org.overture.typechecker.assistant.statement;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AExitStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExitStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
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
