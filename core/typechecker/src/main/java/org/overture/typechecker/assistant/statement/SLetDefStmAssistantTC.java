package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SLetDefStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SLetDefStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(SLetDefStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
