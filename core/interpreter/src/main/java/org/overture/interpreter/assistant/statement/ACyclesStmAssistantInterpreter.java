package org.overture.interpreter.assistant.statement;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ACyclesStmAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACyclesStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static PExp findExpression(ACyclesStm stm, int lineno)
//	{
//		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
//	}

//	public static PStm findStatement(ACyclesStm stm, int lineno)
//	{
//		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
//	}

}
