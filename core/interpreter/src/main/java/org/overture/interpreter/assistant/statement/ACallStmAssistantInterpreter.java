package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACallStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.ACallStmAssistantTC;

public class ACallStmAssistantInterpreter extends ACallStmAssistantTC
{

	public static PExp findExpression(ACallStm stm, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(stm.getArgs(),lineno);
	}

	

}
