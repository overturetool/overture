package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.typechecker.assistant.statement.AAlwaysStmAssistantTC;

public class AAlwaysStmAssistantInterpreter extends AAlwaysStmAssistantTC
{

	public static PExp findExpression(AAlwaysStm stm, int lineno)
	{
		PExp found = PStmAssistantInterpreter.findExpression(stm.getAlways(),lineno);
		if (found != null) return found;
		return PStmAssistantInterpreter.findExpression(stm.getBody(),lineno);
	}

}
