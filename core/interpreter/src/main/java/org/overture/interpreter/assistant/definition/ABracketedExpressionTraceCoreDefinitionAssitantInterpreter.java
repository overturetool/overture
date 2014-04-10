package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ABracketedExpressionTraceCoreDefinitionAssitantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ABracketedExpressionTraceCoreDefinitionAssitantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static TraceNode expand(
//			ABracketedExpressionTraceCoreDefinition core, Context ctxt)
//	{
//		SequenceTraceNode node = new SequenceTraceNode();
//
//		for (ATraceDefinitionTerm term : core.getTerms())
//		{
//			node.nodes.add(ATraceDefinitionTermAssistantInterpreter.expand(term, ctxt));
//		}
//
//		return node;
//	}

}
