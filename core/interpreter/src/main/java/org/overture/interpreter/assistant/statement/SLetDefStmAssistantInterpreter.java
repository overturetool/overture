package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.SLetDefStm;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.typechecker.assistant.statement.SLetDefStmAssistantTC;

public class SLetDefStmAssistantInterpreter extends SLetDefStmAssistantTC
{

	public static PExp findExpression(SLetDefStm stm, int lineno)
	{
		PExp found = PDefinitionListAssistantInterpreter.findExpression(stm.getLocalDefs(),lineno);
		if (found != null) return found;

		return PStmAssistantInterpreter.findExpression(stm.getStatement(),lineno);
	}

}
