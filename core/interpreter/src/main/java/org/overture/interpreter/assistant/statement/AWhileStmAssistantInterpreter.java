package org.overture.interpreter.assistant.statement;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AWhileStmAssistantInterpreter // extends AWhileStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AWhileStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static PExp findExpression(AWhileStm stm, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpression(stm.getExp(), lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
//	}

//	public static PStm findStatement(AWhileStm stm, int lineno)
//	{
//		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
//	}

}
