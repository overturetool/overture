package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.typechecker.assistant.definition.AThreadDefinitionAssistantTC;

public class AThreadDefinitionAssistantInterpreter extends
		AThreadDefinitionAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AThreadDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(AThreadDefinition d,
//			Context initialContext)
//	{
//		return PDefinitionAssistantInterpreter.getNamedValues(d.getOperationDef(), initialContext);
//	}

//	public static PExp findExpression(AThreadDefinition d, int lineno)
//	{
//		return PStmAssistantInterpreter.findExpression(d.getStatement(), lineno);
//	}

//	public static PStm findStatement(AThreadDefinition d, int lineno)
//	{
//		return PStmAssistantInterpreter.findStatement(d.getStatement(), lineno);
//	}

}
