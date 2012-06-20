package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AExitStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AExitStmAssistantTC;

public class AExitStmAssistantInterpreter extends AExitStmAssistantTC
{

	public static PExp findExpression(AExitStm stm, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(stm.getExpression(),lineno);
	}

}
