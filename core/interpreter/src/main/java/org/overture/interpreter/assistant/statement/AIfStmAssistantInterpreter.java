package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.typechecker.assistant.statement.AIfStmAssistantTC;

public class AIfStmAssistantInterpreter extends AIfStmAssistantTC
{

	public static PExp findExpression(AIfStm stm, int lineno)
	{
		PExp found = PStmAssistantInterpreter.findExpression(stm.getThenStm(),lineno);
		if (found != null) return found;

		for (AElseIfStm stmt: stm.getElseIf())
		{
			found = AElseIfStmAssistantInterpreter.findExpression(stmt,lineno);
			if (found != null) return found;
		}

		if (stm.getElseStm() != null)
		{
			found = PStmAssistantInterpreter.findExpression(stm.getElseStm(),lineno);
		}

		return found;
	}

}
