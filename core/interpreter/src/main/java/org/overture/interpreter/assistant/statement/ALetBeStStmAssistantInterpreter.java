package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.ALetBeStStmAssistantTC;

public class ALetBeStStmAssistantInterpreter extends ALetBeStStmAssistantTC
{

	public static PExp findExpression(ALetBeStStm stm, int lineno)
	{
		if (stm.getSuchThat() != null)
		{
			PExp found = PExpAssistantInterpreter.findExpression(stm.getSuchThat(),lineno);
			if (found != null) return found;
		}

		return PStmAssistantInterpreter.findExpression(stm.getStatement(),lineno);
	}

	public static PStm findStatement(ALetBeStStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null) return found;
		return PStmAssistantInterpreter.findStatement(stm.getStatement(),lineno);
	}

}
