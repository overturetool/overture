package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AForPatternBindStmAssitantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AForPatternBindStmAssitantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(AForPatternBindStm statement) {
		return PStmAssistantTC.exitCheck(statement.getStatement());
	}

}
