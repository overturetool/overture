package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.PDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;

public class SLetDefStmAssistantInterpreter// extends SLetDefStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SLetDefStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

	public static PExp findExpression(ALetStm stm, int lineno)
	{
		PExp found = PDefinitionListAssistantInterpreter.findExpression(stm.getLocalDefs(), lineno);
		if (found != null)
			return found;

		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
	}

	public static PStm findStatement(ALetStm stm, int lineno)
	{
		PStm found = PStmAssistantInterpreter.findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;

		found = PDefinitionAssistantInterpreter.findStatement(stm.getLocalDefs(), lineno);
		if (found != null)
			return found;

		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}

}
