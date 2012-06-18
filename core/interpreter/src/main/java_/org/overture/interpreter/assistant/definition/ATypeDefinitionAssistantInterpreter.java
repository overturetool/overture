package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.ATypeDefinition;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;

public class ATypeDefinitionAssistantInterpreter extends
		ATypeDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(ATypeDefinition d,
			RootContext ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();

		if (d.getInvdef() != null)
		{
			FunctionValue invfunc =	new FunctionValue(d.getInvdef(), null, null, ctxt);
			nvl.add(new NameValuePair(d.getInvdef().getName(), invfunc));
		}

		return nvl;
	}

}
