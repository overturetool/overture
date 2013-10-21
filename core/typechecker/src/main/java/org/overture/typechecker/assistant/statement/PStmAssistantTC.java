package org.overture.typechecker.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.statements.PStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PStmAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PTypeSet exitCheck(PStm statement)
	{
		try
		{
			return statement.apply(af.getExitTypeCollector());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new PTypeSet();
		}

	}

}
