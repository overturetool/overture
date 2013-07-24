package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.SequenceTraceNode;
import org.overture.interpreter.traces.TraceNode;

public class ABracketedExpressionTraceCoreDefinitionAssitantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ABracketedExpressionTraceCoreDefinitionAssitantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static TraceNode expand(
			ABracketedExpressionTraceCoreDefinition core, Context ctxt)
	{
		SequenceTraceNode node = new SequenceTraceNode();

		for (ATraceDefinitionTerm term : core.getTerms())
		{
			node.nodes.add(ATraceDefinitionTermAssistantInterpreter.expand(term, ctxt));
		}

		return node;
	}

}
