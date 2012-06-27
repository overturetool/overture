package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AForIndexStmAssistantTC;

public class AForIndexStmAssistantInterpreter extends AForIndexStmAssistantTC
{

	public static PExp findExpression(AForIndexStm stm, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(stm.getFrom(),lineno);
		if (found != null) return found;
		found = PExpAssistantInterpreter.findExpression(stm.getTo(),lineno);
		if (found != null) return found;
		found = PExpAssistantInterpreter.findExpression(stm.getBy(),lineno);
		if (found != null) return found;
		return PStmAssistantInterpreter.findExpression(stm.getStatement(),lineno);
	}

	public static PStm findStatement(AForIndexStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null) return found;
		return PStmAssistantInterpreter.findStatement(stm.getStatement(),lineno);
	}

}
