package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.AUntypedDefinitionAssistantTC;

public class AUntypedDefinitionAssistantInterpreter extends
		AUntypedDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(AUntypedDefinition d,
			RootContext ctxt)
	{
		assert false: "Can't get name/values of untyped definition?";
		return null;
	}

}
