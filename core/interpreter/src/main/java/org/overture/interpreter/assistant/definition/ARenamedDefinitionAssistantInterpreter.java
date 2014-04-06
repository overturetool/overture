package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ARenamedDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARenamedDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(ARenamedDefinition d,
//			Context initialContext)
//	{
//		NameValuePairList renamed = new NameValuePairList();
//
//		for (NameValuePair nv : PDefinitionAssistantInterpreter.getNamedValues(d.getDef(), initialContext))
//		{
//			// We exclude any name from the definition other than the one
//			// explicitly renamed. Otherwise, generated names like pre_f
//			// come through and are not renamed.
//
//			if (nv.name.equals(d.getDef().getName()))
//			{
//				renamed.add(new NameValuePair(d.getName(), nv.value));
//			}
//		}
//
//		return renamed;
//	}

//	public static boolean isTypeDefinition(ARenamedDefinition def)
//	{
//		return PDefinitionAssistantInterpreter.isTypeDefinition(def.getDef());
//	}

}
