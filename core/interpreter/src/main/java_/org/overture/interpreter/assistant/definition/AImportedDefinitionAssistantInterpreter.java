package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AImportedDefinition;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.AImportedDefinitionAssistantTC;

public class AImportedDefinitionAssistantInterpreter extends
		AImportedDefinitionAssistantTC
{
 
	public static NameValuePairList getNamedValues(AImportedDefinition d,
			RootContext ctxt)
	{
		NameValuePairList renamed = new NameValuePairList();

		for (NameValuePair nv: PDefinitionAssistantInterpreter.getNamedValues(d.getDef(), ctxt))
		{
			if (nv.name.equals(d.getDef().getName()))	// NB. excludes pre/post/inv functions
			{
				renamed.add(new NameValuePair(d.getName(), nv.value));
			}
		}

		return renamed;
	}

}
