package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;

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

}
