package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AForIndexStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AForIndexStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(AForIndexStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
