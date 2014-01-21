package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;

public class AForPatternBindStmAssitantInterpreter // extends
// AForPatternBindStmAssitantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AForPatternBindStmAssitantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

	public static PExp findExpression(AForPatternBindStm stm, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(stm.getExp(), lineno);
		if (found != null)
			return found;
		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
	}

	public static PStm findStatement(AForPatternBindStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}

}
