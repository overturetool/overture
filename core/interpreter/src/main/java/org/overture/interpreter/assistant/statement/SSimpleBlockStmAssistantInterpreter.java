package org.overture.interpreter.assistant.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.VoidValue;

public class SSimpleBlockStmAssistantInterpreter // extends
// SSimpleBlockStmAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSimpleBlockStmAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

	public static Value evalBlock(SSimpleBlockStm node, Context ctxt)
			throws AnalysisException
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

}
