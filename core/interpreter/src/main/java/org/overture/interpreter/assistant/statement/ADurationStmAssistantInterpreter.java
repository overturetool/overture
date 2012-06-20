package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ADurationStm;

public class ADurationStmAssistantInterpreter
{

	public static PExp findExpression(ADurationStm stm, int lineno)
	{
		return PStmAssistantInterpreter.findExpression(stm.getStatement(),lineno);
	}

}
