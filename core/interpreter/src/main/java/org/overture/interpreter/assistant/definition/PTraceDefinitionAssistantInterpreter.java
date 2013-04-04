package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.traces.AInstanceTraceDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;
import org.overture.typechecker.assistant.definition.PTraceDefinitionAssistantTC;

public class PTraceDefinitionAssistantInterpreter extends
		PTraceDefinitionAssistantTC
{

	public static TraceNode expand(PTraceDefinition term, Context ctxt) 
	{
		switch (term.kindPTraceDefinition())
		{
			case AInstanceTraceDefinition.kindPTraceDefinition:
				assert false : "this one is not in Nicks tree";
				return null;
			case ALetBeStBindingTraceDefinition.kindPTraceDefinition:
				return ALetBeStBindingTraceDefinitionAssistantInterpreter.expand((ALetBeStBindingTraceDefinition)term,ctxt);
			case ALetDefBindingTraceDefinition.kindPTraceDefinition:
				return ALetDefBindingTraceDefinitionAssistantInterpreter.expand((ALetDefBindingTraceDefinition)term,ctxt);
			case ARepeatTraceDefinition.kindPTraceDefinition:
				return ARepeatTraceDefinitionAssistantInterpreter.expand((ARepeatTraceDefinition) term,ctxt);
			default:
				break;
		}
		
		return null;
	}

}
