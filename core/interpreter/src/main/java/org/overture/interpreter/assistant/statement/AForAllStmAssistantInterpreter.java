package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.statement.AForAllStmAssistantTC;

public class AForAllStmAssistantInterpreter extends AForAllStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AForAllStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static PExp findExpression(AForAllStm stm, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(stm.getSet(), lineno);
		if (found != null)
			return found;
		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
	}

	public static PStm findStatement(AForAllStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);

	}

}
