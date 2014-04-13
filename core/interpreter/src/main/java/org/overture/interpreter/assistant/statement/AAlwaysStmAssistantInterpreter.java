package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AAlwaysStmAssistantInterpreter // extends AAlwaysStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AAlwaysStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static PExp findExpression(AAlwaysStm stm, int lineno)
//	{
//		PExp found = PStmAssistantInterpreter.findExpression(stm.getAlways(), lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findExpression(stm.getBody(), lineno);
//	}

	public static PStm findStatement(AAlwaysStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		found = PStmAssistantInterpreter.findStatement(stm.getAlways(), lineno);
		if (found != null)
			return found;
		return PStmAssistantInterpreter.findStatement(stm.getBody(), lineno);
	}

}
