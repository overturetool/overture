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

	public TraceNode expand(PTraceDefinition term, Context ctxt)
	{
		try
		{
			return term.apply(af.getTermTraceExpander(), ctxt);
		} catch (AnalysisException e)
		{
			return null;
		}
	}

}
