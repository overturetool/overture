package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;

public class AClassInvariantDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AClassInvariantDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static PExp findExpression(AClassInvariantDefinition def, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(def.getExpression(), lineno);
	}
}
