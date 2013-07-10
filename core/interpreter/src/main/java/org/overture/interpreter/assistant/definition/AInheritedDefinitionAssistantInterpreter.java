package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.AInheritedDefinitionAssistantTC;

public class AInheritedDefinitionAssistantInterpreter extends
		AInheritedDefinitionAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AInheritedDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static NameValuePairList getNamedValues(AInheritedDefinition d,
			Context initialContext)
	{
		NameValuePairList renamed = new NameValuePairList();

		if (d.getSuperdef() instanceof AUntypedDefinition)
		{
			if (d.getClassDefinition() != null)
			{
				d.setSuperdef(PDefinitionAssistantInterpreter.findName(d.getClassDefinition(), d.getSuperdef().getName(), d.getNameScope()));
			}
		}

		for (NameValuePair nv : PDefinitionAssistantInterpreter.getNamedValues(d.getSuperdef(), initialContext))
		{
			renamed.add(new NameValuePair(nv.name.getModifiedName(d.getName().getModule()), nv.value));
		}

		return renamed;
	}

	public static boolean isTypeDefinition(AInheritedDefinition def)
	{
		return PDefinitionAssistantInterpreter.isTypeDefinition(def.getSuperdef());
	}

}
