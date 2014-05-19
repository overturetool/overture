package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AErrorCaseAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AErrorCaseAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static PExp findExpression(AErrorCase err, int lineno)
//	{
//		PExp found = af.createPExpAssistant().findExpression(err.getLeft(), lineno);
//		if (found != null)
//		{
//			return found;
//		}
//		return af.createPExpAssistant().findExpression(err.getRight(), lineno);
//	}

}
