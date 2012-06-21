package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;
import org.overture.interpreter.traces.TraceVariableList;

public class ALetDefBindingTraceDefinitionAssistantInterpreter
{

	public static TraceNode expand(ALetDefBindingTraceDefinition term,
			Context ctxt) 
	{
		Context evalContext = new Context(term.getLocation(), "TRACE", ctxt);

		for (PDefinition d: term.getLocalDefs())
		{
			evalContext.putList(PDefinitionAssistantInterpreter.getNamedValues(d,evalContext));
		}

		TraceNode node = PTraceDefinitionAssistantInterpreter.expand(term.getBody(),evalContext);
		node.setVariables(new TraceVariableList(evalContext, term.getLocalDefs()));
		return node;
	}

}
