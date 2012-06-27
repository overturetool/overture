package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.PStm;

public class ADurationStmAssistantInterpreter
{

	public static PExp findExpression(ADurationStm stm, int lineno)
	{
		return PStmAssistantInterpreter.findExpression(stm.getStatement(),lineno);
	}

	public static PStm findStatement(ADurationStm stm, int lineno)
	{
		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}

}
