package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ALetDefBindingTraceDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALetDefBindingTraceDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static TraceNode expand(ALetDefBindingTraceDefinition term,
//			Context ctxt)
//	{
//		Context evalContext = new Context(af, term.getLocation(), "TRACE", ctxt);
//
//		for (PDefinition d : term.getLocalDefs())
//		{
//			evalContext.putList(PDefinitionAssistantInterpreter.getNamedValues(d, evalContext));
//		}
//
//		TraceNode node = PTraceDefinitionAssistantInterpreter.expand(term.getBody(), evalContext);
//		node.addVariables(new TraceVariableList(evalContext, term.getLocalDefs()));
//		return node;
//	}

}
