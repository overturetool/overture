package org.overture.interpreter.assistant.statement;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AForAllStmAssistantInterpreter// extends AForAllStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AForAllStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static PExp findExpression(AForAllStm stm, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpression(stm.getSet(), lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
//	}

//	public static PStm findStatement(AForAllStm stm, int lineno)
//	{
//		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
//
//	}

}
