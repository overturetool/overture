package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.ATrapStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ATrapStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATrapStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(ATrapStm statement) {
		
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistantTC.exitCheck(statement.getBody()));
		types.addAll(PStmAssistantTC.exitCheck(statement.getWith()));
		return types;
	}

}
