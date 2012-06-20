package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AReturnStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AReturnStmAssistantTC;

public class AReturnStmAssistantInterpreter extends AReturnStmAssistantTC
{

	public static PExp findExpression(AReturnStm stm, int lineno)
	{
		return stm.getExpression() == null ? null : PExpAssistantInterpreter.findExpression(stm.getExpression(),lineno);
	}

}
