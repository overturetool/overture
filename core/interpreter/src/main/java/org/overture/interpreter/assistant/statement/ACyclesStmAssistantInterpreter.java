package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.PStm;

public class ACyclesStmAssistantInterpreter
{

	public static PExp findExpression(ACyclesStm stm, int lineno)
	{
		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
	}

	public static PStm findStatement(ACyclesStm stm, int lineno)
	{
		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}

}
