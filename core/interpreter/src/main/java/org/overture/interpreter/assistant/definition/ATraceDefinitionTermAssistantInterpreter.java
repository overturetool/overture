package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.AlternativeTraceNode;
import org.overture.interpreter.traces.TraceNode;

public class ATraceDefinitionTermAssistantInterpreter
{

	public static TraceNode expand(ATraceDefinitionTerm terms, Context ctxt) 
	{
		AlternativeTraceNode node = new AlternativeTraceNode();

		for (PTraceDefinition term: terms.getList())
		{
			node.alternatives.add(PTraceDefinitionAssistantInterpreter.expand(term,ctxt));
		}

		return node;
	}

}
