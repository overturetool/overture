package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AWhileStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AWhileStmAssistantTC;

public class AWhileStmAssistantInterpreter extends AWhileStmAssistantTC
{

	public static PExp findExpression(AWhileStm stm, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(stm.getExp(),lineno);
		if (found != null) return found;
		return PStmAssistantInterpreter.findExpression(stm.getStatement(),lineno);
	}

}
