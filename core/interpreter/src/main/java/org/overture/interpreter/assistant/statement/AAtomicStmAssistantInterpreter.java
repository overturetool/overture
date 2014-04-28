package org.overture.interpreter.assistant.statement;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AAtomicStmAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AAtomicStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static PExp findExpression(AAtomicStm stm, int lineno)
//	{
//		PExp found = null;
//
//		for (AAssignmentStm stmt : stm.getAssignments())
//		{
//			found = AAssignmentStmAssistantInterpreter.findExpression(stmt, lineno);
//			if (found != null)
//				break;
//		}
//
//		return found;
//	}

//	public static PStm findStatement(AAtomicStm stm, int lineno)
//	{
//		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
//		if (found != null)
//			return found;
//
//		for (AAssignmentStm stmt : stm.getAssignments())
//		{
//			found = PStmAssistantInterpreter.findStatement(stmt, lineno);
//			if (found != null)
//				break;
//		}
//
//		return found;
//	}

}
