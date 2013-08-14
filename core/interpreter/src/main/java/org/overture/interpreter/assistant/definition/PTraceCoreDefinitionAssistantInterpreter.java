package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
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

	public static TraceNode expand(PTraceCoreDefinition core, Context ctxt)
	{
		if (core instanceof AApplyExpressionTraceCoreDefinition) {
			return AApplyExpressionTraceCoreDefinitionAssistantInterpreter.expand((AApplyExpressionTraceCoreDefinition) core, ctxt);
		} else if (core instanceof ABracketedExpressionTraceCoreDefinition) {
			return ABracketedExpressionTraceCoreDefinitionAssitantInterpreter.expand((ABracketedExpressionTraceCoreDefinition) core, ctxt);
		} else if (core instanceof AConcurrentExpressionTraceCoreDefinition) {
			return AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter.expand((AConcurrentExpressionTraceCoreDefinition) core, ctxt);
		} else {
			assert false : "Should not happen";
			return null;
		}
	}

}
