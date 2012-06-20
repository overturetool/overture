package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;

public class AInheritedDefinitionAssistantInterpreter
{

	public static NameValuePairList getNamedValues(AInheritedDefinition d,
			Context initialContext)
	{
		NameValuePairList renamed = new NameValuePairList();

		if (d.getSuperdef() instanceof AUntypedDefinition)
		{
			if (d.getClassDefinition() != null)
			{
				d.setSuperdef(PDefinitionAssistantInterpreter.findName(d.getClassDefinition(),d.getSuperdef().getName(), d.getNameScope()));
			}
		}

		for (NameValuePair nv: PDefinitionAssistantInterpreter.getNamedValues(d.getSuperdef(), initialContext))
		{
			renamed.add(new NameValuePair(
				nv.name.getModifiedName(d.getName().module), nv.value));
		}

		return renamed;
	}

}
