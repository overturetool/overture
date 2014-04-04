package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AInheritedDefinitionAssistantInterpreter

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AInheritedDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(AInheritedDefinition d,
//			Context initialContext)
//	{
//		NameValuePairList renamed = new NameValuePairList();
//
//		if (d.getSuperdef() instanceof AUntypedDefinition)
//		{
//			if (d.getClassDefinition() != null)
//			{
//				d.setSuperdef(af.createPDefinitionAssistant().findName(d.getClassDefinition(), d.getSuperdef().getName(), d.getNameScope()));
//			}
//		}
//
//		for (NameValuePair nv : PDefinitionAssistantInterpreter.getNamedValues(d.getSuperdef(), initialContext))
//		{
//			renamed.add(new NameValuePair(nv.name.getModifiedName(d.getName().getModule()), nv.value));
//		}
//
//		return renamed;
//	}

//	public static boolean isTypeDefinition(AInheritedDefinition def)
//	{
//		return PDefinitionAssistantInterpreter.isTypeDefinition(def.getSuperdef());
//	}

}
