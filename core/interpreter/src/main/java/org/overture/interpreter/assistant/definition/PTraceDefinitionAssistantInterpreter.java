package org.overture.interpreter.assistant.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;
import org.overture.typechecker.assistant.definition.PTraceDefinitionAssistantTC;

public class PTraceDefinitionAssistantInterpreter extends
		PTraceDefinitionAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTraceDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static TraceNode expand(PTraceDefinition term, Context ctxt)
	{
		try
		{
			return term.apply(af.getTermTraceExpander(), ctxt);
		} catch (AnalysisException e)
		{
			return null;
		}
//		if (term instanceof AInstanceTraceDefinition)
//		{
//			assert false : "this one is not in Nicks tree";
//			return null;
//		} else if (term instanceof ALetBeStBindingTraceDefinition)
//		{
//			return ALetBeStBindingTraceDefinitionAssistantInterpreter.expand((ALetBeStBindingTraceDefinition) term, ctxt);
//		} else if (term instanceof ALetDefBindingTraceDefinition)
//		{
//			return ALetDefBindingTraceDefinitionAssistantInterpreter.expand((ALetDefBindingTraceDefinition) term, ctxt);
//		} else if (term instanceof ARepeatTraceDefinition)
//		{
//			return ARepeatTraceDefinitionAssistantInterpreter.expand((ARepeatTraceDefinition) term, ctxt);
//		} else
//		{
//		}
//
//		return null;
	}

}
