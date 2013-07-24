package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ALetBeStStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALetBeStStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(ALetBeStStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
