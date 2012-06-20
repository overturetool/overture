package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ASetEnumSetExpAssistantTC;

public class ASetEnumSetExpAssistantInterpreter extends
		ASetEnumSetExpAssistantTC
{

	public static ValueList getValues(ASetEnumSetExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getMembers(),ctxt);
	}

	public static PExp findExpression(ASetEnumSetExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getMembers(),lineno);
	}

}
