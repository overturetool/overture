package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.ATypeDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;

public class ATypeDefinitionAssistantInterpreter extends
		ATypeDefinitionAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATypeDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(ATypeDefinition d,
//			Context initialContext)
//	{
//		NameValuePairList nvl = new NameValuePairList();
//
//		if (d.getInvdef() != null)
//		{
//			FunctionValue invfunc = new FunctionValue(d.getInvdef(), null, null, initialContext);
//			nvl.add(new NameValuePair(d.getInvdef().getName(), invfunc));
//		}
//
//		return nvl;
//	}

//	public static PExp findExpression(ATypeDefinition d, int lineno)
//	{
//		if (d.getInvdef() != null)
//		{
//			PExp found = PDefinitionAssistantInterpreter.findExpression(d.getInvdef(), lineno);
//			if (found != null)
//			{
//				return found;
//			}
//		}
//
//		return null;
//	}

//	public static boolean isTypeDefinition(ATypeDefinition def)
//	{
//		return true;
//	}

}
