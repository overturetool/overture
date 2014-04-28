package org.overture.interpreter.assistant.statement;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ALetBeStStmAssistantInterpreter // extends ALetBeStStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALetBeStStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static PExp findExpression(ALetBeStStm stm, int lineno)
//	{
//		if (stm.getSuchThat() != null)
//		{
//			PExp found = PExpAssistantInterpreter.findExpression(stm.getSuchThat(), lineno);
//			if (found != null)
//				return found;
//		}
//
//		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
//	}

//	public static PStm findStatement(ALetBeStStm stm, int lineno)
//	{
//		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
//	}

}
