package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.ACallObjectStatementAssistantTC;

public class ACallObjectStatementAssistantInterpreter extends
		ACallObjectStatementAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACallObjectStatementAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static PExp findExpression(ACallObjectStm stm, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(stm.getArgs(), lineno);
	}

}
