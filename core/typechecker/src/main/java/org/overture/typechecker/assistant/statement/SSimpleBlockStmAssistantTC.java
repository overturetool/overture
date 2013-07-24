package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SSimpleBlockStmAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSimpleBlockStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PTypeSet exitCheck(SSimpleBlockStm statement)
	{
		PTypeSet types = new PTypeSet();

		for (PStm stmt : statement.getStatements())
		{
			types.addAll(PStmAssistantTC.exitCheck(stmt));
		}

		return types;
	}

}
