package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.AThreadDefinitionAssistantTC;

public class AThreadDefinitionAssistantInterpreter extends
		AThreadDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(AThreadDefinition d,
			Context initialContext) 
	{
		return PDefinitionAssistantInterpreter.getNamedValues(d.getOperationDef(), initialContext);
	}

	public static PExp findExpression(AThreadDefinition d, int lineno)
	{
		return PStmAssistantInterpreter.findExpression(d.getStatement(),lineno);
	}

}
