package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AImportedDefinitionAssistantInterpreter 
{

	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AImportedDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(AImportedDefinition d,
//			Context initialContext)
//	{
//		NameValuePairList renamed = new NameValuePairList();
//
//		for (NameValuePair nv : PDefinitionAssistantInterpreter.getNamedValues(d.getDef(), initialContext))
//		{
//			if (nv.name.equals(d.getDef().getName())) // NB. excludes pre/post/inv functions
//			{
//				renamed.add(new NameValuePair(d.getName(), nv.value));
//			}
//		}
//
//		return renamed;
//	}

//	public static boolean isTypeDefinition(AImportedDefinition def)
//	{
//		return PDefinitionAssistantInterpreter.isTypeDefinition(def.getDef());
//	}

}
