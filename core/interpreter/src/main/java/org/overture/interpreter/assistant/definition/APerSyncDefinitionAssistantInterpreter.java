package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.typechecker.assistant.definition.APerSyncDefinitionAssistantTC;

public class APerSyncDefinitionAssistantInterpreter extends
		APerSyncDefinitionAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public APerSyncDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static PExp findExpression(APerSyncDefinition d, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(d.getGuard(), lineno);
	}

}
