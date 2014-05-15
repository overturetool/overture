package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ARepeatTraceDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARepeatTraceDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static TraceNode expand(ARepeatTraceDefinition term, Context ctxt)
//	{
//		TraceNode body = PTraceCoreDefinitionAssistantInterpreter.expand(term.getCore(), ctxt);
//
//		if (term.getFrom() == 1 && term.getTo() == 1)
//		{
//			return body;
//		} else
//		{
//			return new RepeatTraceNode(body, term.getFrom(), term.getTo());
//		}
//	}

}
