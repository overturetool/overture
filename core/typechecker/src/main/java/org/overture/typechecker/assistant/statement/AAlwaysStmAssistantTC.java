package org.overture.typechecker.assistant.statement;


import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AAlwaysStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AAlwaysStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(AAlwaysStm statement) {
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistantTC.exitCheck(statement.getBody()));
		types.addAll(PStmAssistantTC.exitCheck(statement.getAlways()));
		return types;
	}

}
