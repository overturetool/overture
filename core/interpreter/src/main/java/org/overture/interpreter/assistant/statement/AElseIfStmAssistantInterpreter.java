package org.overture.interpreter.assistant.statement;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AElseIfStmAssistantInterpreter // extends AElseIfStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AElseIfStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static PExp findExpression(AElseIfStm stm, int lineno)
//	{
//		return PExpAssistantInterpreter.findExpression(stm.getElseIf(), lineno);
//	}

//	public static PStm findStatement(AElseIfStm stm, int lineno)
//	{
//		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findStatement(stm.getThenStm(), lineno);
//	}

}
