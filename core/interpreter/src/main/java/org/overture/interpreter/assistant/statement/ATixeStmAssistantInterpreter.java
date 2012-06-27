package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.PStm;

public class ATixeStmAssistantInterpreter
{

	public static PExp findExpression(ATixeStm stm, int lineno)
	{
		PExp found = PStmAssistantInterpreter.findExpression(stm.getBody(),lineno);
		if (found != null) return found;

		for (ATixeStmtAlternative tsa: stm.getTraps())
		{
			found = PStmAssistantInterpreter.findExpression(tsa.getStatement(),lineno);
			if (found != null) break;
		}

		return found;
	}

	public static PStm findStatement(ATixeStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null) return found;
		found = PStmAssistantInterpreter.findStatement(stm.getBody(),lineno);
		if (found != null) return found;

		for (ATixeStmtAlternative tsa: stm.getTraps())
		{
			found = PStmAssistantInterpreter.findStatement(tsa.getStatement(),lineno);
			if (found != null) break;
		}

		return found;
	}

}
