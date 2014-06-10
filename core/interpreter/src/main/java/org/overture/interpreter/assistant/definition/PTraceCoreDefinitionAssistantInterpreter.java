package org.overture.interpreter.assistant.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;

public class PTraceCoreDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTraceCoreDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public TraceNode expand(PTraceCoreDefinition core, Context ctxt)
	{
		try
		{
			return core.apply(af.getTraceExpander(), ctxt);
		} catch (AnalysisException e)
		{
			return null;
		}
//		if (core instanceof AApplyExpressionTraceCoreDefinition)
//		{
//			return AApplyExpressionTraceCoreDefinitionAssistantInterpreter.expand((AApplyExpressionTraceCoreDefinition) core, ctxt);
//		} else if (core instanceof ABracketedExpressionTraceCoreDefinition)
//		{
//			return ABracketedExpressionTraceCoreDefinitionAssitantInterpreter.expand((ABracketedExpressionTraceCoreDefinition) core, ctxt);
//		} else if (core instanceof AConcurrentExpressionTraceCoreDefinition)
//		{
//			return AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter.expand((AConcurrentExpressionTraceCoreDefinition) core, ctxt);
//		} else
//		{
//			assert false : "Should not happen";
//			return null;
//		}
	}

}
