package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static TraceNode expand(
//			AConcurrentExpressionTraceCoreDefinition core, Context ctxt)
//	{
//		ConcurrentTraceNode node = new ConcurrentTraceNode();
//
//		for (PTraceDefinition term : core.getDefs())
//		{
//			node.nodes.add(PTraceDefinitionAssistantInterpreter.expand(term, ctxt));
//		}
//
//		return node;
//	}

}
