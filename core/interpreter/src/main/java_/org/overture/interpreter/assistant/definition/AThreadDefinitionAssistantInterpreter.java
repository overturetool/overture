package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AThreadDefinition;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.AThreadDefinitionAssistantTC;

public class AThreadDefinitionAssistantInterpreter extends
		AThreadDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(AThreadDefinition d,
			RootContext ctxt)
	{
		return PDefinitionAssistantInterpreter.getNamedValues(d.getOperationDef(), ctxt);
	}

}
