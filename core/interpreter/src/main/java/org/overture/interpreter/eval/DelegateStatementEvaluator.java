package org.overture.interpreter.eval;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.Value;

public class DelegateStatementEvaluator extends StatementEvaluator
{

	@Override
	public Value caseANotYetSpecifiedStm(ANotYetSpecifiedStm node, Context ctxt)
			throws AnalysisException
	{
		Value val;

		val = evalDelegatedANotYetSpecified(node, node.getLocation(), 4041, "statement", false, ctxt);
		if (val != null)
		{
			return val;
		}

		if (val == null && node.getLocation().getModule().equals("CPU"))
		{
			if (ctxt.title.equals("deploy(obj)"))
			{
				return ctxt.assistantFactory.createACpuClassDefinitionAssistant().deploy(node, ctxt);
			} else if (ctxt.title.equals("deploy(obj, name)"))
			{
				return ctxt.assistantFactory.createACpuClassDefinitionAssistant().deploy(node, ctxt);
			} else if (ctxt.title.equals("setPriority(opname, priority)"))
			{
				return ctxt.assistantFactory.createACpuClassDefinitionAssistant().setPriority(node, ctxt);
			}
		}

		return VdmRuntimeError.abort(node.getLocation(), 4041, "'is not yet specified' statement reached", ctxt);
	}
}
