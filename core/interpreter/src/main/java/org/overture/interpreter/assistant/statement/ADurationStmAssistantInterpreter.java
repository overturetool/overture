package org.overture.interpreter.assistant.statement;

import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ADurationStmAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ADurationStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static PExp findExpression(ADurationStm stm, int lineno)
//	{
//		return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
//	}

	public static PStm findStatement(ADurationStm stm, int lineno)
	{
		return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}

}
