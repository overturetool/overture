package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.VoidValue;

public class SSimpleBlockStmAssistantInterpreter
{

	public static Value evalBlock(SSimpleBlockStm node, Context ctxt) throws Throwable
	{
		// Note, no breakpoint check - designed to be called by eval

		for (PStm s : node.getStatements())
		{
			Value rv = s.apply(VdmRuntime.getStatementEvaluator(), ctxt);

			if (!rv.isVoid())
			{
				return rv;
			}
		}

		return new VoidValue();
	}

	public static PExp findExpression(SSimpleBlockStm stm, int lineno)
	{
		PExp found = null;

		for (PStm stmt: stm.getStatements())
		{
			found = PStmAssistantInterpreter.findExpression(stmt,lineno);
			if (found != null) break;
		}

		return found;
	}

}
