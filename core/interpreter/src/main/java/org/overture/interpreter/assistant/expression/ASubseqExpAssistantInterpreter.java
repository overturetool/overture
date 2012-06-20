package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ASubseqExpAssistantTC;

public class ASubseqExpAssistantInterpreter extends ASubseqExpAssistantTC
{

	public static ValueList getValues(ASubseqExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getSeq(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getFrom(),ctxt));
		list.addAll(PExpAssistantInterpreter.getValues(exp.getTo(),ctxt));
		return list;
	}

	public static PExp findExpression(ASubseqExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getSeq(),lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getFrom(),lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getTo(),lineno);
		if (found != null) return found;

		return null;
	}

}
