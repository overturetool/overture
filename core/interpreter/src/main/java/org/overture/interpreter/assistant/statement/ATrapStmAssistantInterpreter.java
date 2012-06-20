package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ATrapStm;
import org.overture.typechecker.assistant.statement.ATrapStmAssistantTC;

public class ATrapStmAssistantInterpreter extends ATrapStmAssistantTC
{

	public static PExp findExpression(ATrapStm stm, int lineno)
	{
		PExp found = PStmAssistantInterpreter.findExpression(stm.getBody(),lineno);
		if (found != null) return found;
		return PStmAssistantInterpreter.findExpression(stm.getWith(),lineno);
	}

}
