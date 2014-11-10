package org.overture.interpreter.assistant.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;

public class PTraceCoreDefinitionAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTraceCoreDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	//FIXME: only used once. inline it
	public TraceNode expand(PTraceCoreDefinition core, Context ctxt)
	{
		try
		{
			return core.apply(af.getTraceExpander(), ctxt);
		} catch (AnalysisException e)
		{
			return null;
		}
	}

}
