package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.traces.TraceNode;

public class PTraceCoreDefinitionAssistantInterpreter
{

	public static TraceNode expand(PTraceCoreDefinition core, Context ctxt)
	{
		switch (core.kindPTraceCoreDefinition())
		{
			case AApplyExpressionTraceCoreDefinition.kindPTraceCoreDefinition:
				return AApplyExpressionTraceCoreDefinitionAssistantInterpreter.expand((AApplyExpressionTraceCoreDefinition)core,ctxt);
			case ABracketedExpressionTraceCoreDefinition.kindPTraceCoreDefinition:
				return ABracketedExpressionTraceCoreDefinitionAssitantInterpreter.expand((ABracketedExpressionTraceCoreDefinition)core,ctxt);
			case AConcurrentExpressionTraceCoreDefinition.kindPTraceCoreDefinition:
				return AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter.expand((AConcurrentExpressionTraceCoreDefinition)core,ctxt);
			default:
				assert false : "Should not happen";
				return null;
		}
	}

}
