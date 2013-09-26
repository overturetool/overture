package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AElseIfStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AElseIfStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(AElseIfStm statement) {
		return PStmAssistantTC.exitCheck(statement.getThenStm());
	}

}
