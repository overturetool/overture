package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AWhileStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AWhileStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AWhileStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(AWhileStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
