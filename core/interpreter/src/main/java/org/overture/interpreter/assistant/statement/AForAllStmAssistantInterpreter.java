package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AForAllStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AForAllStmAssistantTC;

public class AForAllStmAssistantInterpreter extends AForAllStmAssistantTC
{

	public static PExp findExpression(AForAllStm stm, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(stm.getSet(),lineno);
		if (found != null) return found;
		return PStmAssistantInterpreter.findExpression(stm.getStatement(),lineno);
	}
 
}
