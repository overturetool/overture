package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AForAllStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AForAllStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AForAllStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(AForAllStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
