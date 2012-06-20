package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.typechecker.assistant.statement.ACasesStmAssistantTC;

public class ACasesStmAssistantInterpreter extends ACasesStmAssistantTC
{

	public static PExp findExpression(ACasesStm stm, int lineno)
	{
		PExp found = null;

		for (ACaseAlternativeStm stmt: stm.getCases())
		{
			found = PStmAssistantInterpreter.findExpression(stmt.getResult(),lineno);
			if (found != null) break;
		}

		return found;
	}

}
