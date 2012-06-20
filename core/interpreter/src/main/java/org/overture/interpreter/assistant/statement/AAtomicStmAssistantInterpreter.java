package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;

public class AAtomicStmAssistantInterpreter
{

	public static PExp findExpression(AAtomicStm stm, int lineno)
	{
		PExp found = null;

		for (AAssignmentStm stmt: stm.getAssignments())
		{
			found = AAssignmentStmAssistantInterpreter.findExpression(stmt,lineno);
			if (found != null) break;
		}

		return found;
	}

}
