package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ATraceDefinitionTermAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATraceDefinitionTermAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static TraceNode expand(ATraceDefinitionTerm terms, Context ctxt)
//	{
//		AlternativeTraceNode node = new AlternativeTraceNode();
//
//		for (PTraceDefinition term : terms.getList())
//		{
//			node.alternatives.add(PTraceDefinitionAssistantInterpreter.expand(term, ctxt));
//		}
//
//		return node;
//	}

}
