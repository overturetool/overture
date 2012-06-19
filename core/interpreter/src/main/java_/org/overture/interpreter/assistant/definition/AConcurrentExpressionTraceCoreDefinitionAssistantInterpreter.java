package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.ConcurrentTraceNode;
import org.overture.interpreter.traces.TraceNode;

public class AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter
{

	public static TraceNode expand(
			AConcurrentExpressionTraceCoreDefinition core, Context ctxt)
	{
		ConcurrentTraceNode node = new ConcurrentTraceNode();

		for (PTraceDefinition term: core.getDefs())
		{
			node.nodes.add(PTraceDefinitionAssistantInterpreter.expand(term, ctxt));
		}

		return node;
	}

}
