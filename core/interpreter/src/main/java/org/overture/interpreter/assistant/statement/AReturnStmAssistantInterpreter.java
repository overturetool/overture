package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AReturnStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;

public class AReturnStmAssistantInterpreter // extends AReturnStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AReturnStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

	public static PExp findExpression(AReturnStm stm, int lineno)
	{
		return stm.getExpression() == null ? null
				: PExpAssistantInterpreter.findExpression(stm.getExpression(), lineno);
	}

}
