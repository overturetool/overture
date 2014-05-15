package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;

public class AForIndexStmAssistantInterpreter// extends AForIndexStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AForIndexStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static PExp findExpression(AForIndexStm stm, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpression(stm.getFrom(), lineno);
//		if (found != null)
//			return found;
//		found = PExpAssistantInterpreter.findExpression(stm.getTo(), lineno);
//		if (found != null)
//			return found;
//		found = PExpAssistantInterpreter.findExpression(stm.getBy(), lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
//	}

//	public static PStm findStatement(AForIndexStm stm, int lineno)
//	{
//		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
//		if (found != null)
//			return found;
//		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
//	}

}
