package org.overture.interpreter.assistant.statement;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AExitStmAssistantInterpreter // extends AExitStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExitStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static PExp findExpression(AExitStm stm, int lineno)
//	{
//		return stm.getExpression() == null ? null
//				: PExpAssistantInterpreter.findExpression(stm.getExpression(), lineno);
//	}

}
