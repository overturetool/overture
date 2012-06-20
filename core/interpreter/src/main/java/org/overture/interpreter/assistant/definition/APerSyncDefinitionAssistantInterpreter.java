package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.definition.APerSyncDefinitionAssistantTC;

public class APerSyncDefinitionAssistantInterpreter extends
		APerSyncDefinitionAssistantTC
{

	public static PExp findExpression(APerSyncDefinition d, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(d.getGuard(),lineno);
	}

}
