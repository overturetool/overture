package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACyclesStm;

public class ACyclesStmAssistantInterpreter
{

	public static PExp findExpression(ACyclesStm stm, int lineno)
	{
		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
	}

}
