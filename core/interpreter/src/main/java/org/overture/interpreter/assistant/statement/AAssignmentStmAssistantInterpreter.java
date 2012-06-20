package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AAssignmentStmAssistantTC;

public class AAssignmentStmAssistantInterpreter extends
		AAssignmentStmAssistantTC
{

	public static PExp findExpression(AAssignmentStm stm, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(stm.getExp(),lineno);
	}

}
