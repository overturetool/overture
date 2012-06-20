package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AElseIfStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AElseIfStmAssistantTC;

public class AElseIfStmAssistantInterpreter extends AElseIfStmAssistantTC
{

	public static PExp findExpression(AElseIfStm stm, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(stm.getElseIf(), lineno);
	}

}
