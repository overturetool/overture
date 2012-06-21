package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.RepeatTraceNode;
import org.overture.interpreter.traces.TraceNode;

public class ARepeatTraceDefinitionAssistantInterpreter
{

	public static TraceNode expand(ARepeatTraceDefinition term, Context ctxt) 
	{
		TraceNode body = PTraceCoreDefinitionAssistantInterpreter.expand(term.getCore(),ctxt);

		if (term.getFrom() == 1 && term.getTo() == 1)
		{
			return body;
		}
		else
		{
			return new RepeatTraceNode(body, term.getFrom(), term.getTo());
		}
	}

}
