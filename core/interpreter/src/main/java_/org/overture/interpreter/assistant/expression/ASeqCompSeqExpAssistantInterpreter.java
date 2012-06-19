package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.pattern.ASetBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ASeqCompSeqExpAssistantTC;

public class ASeqCompSeqExpAssistantInterpreter extends
		ASeqCompSeqExpAssistantTC
{

	public static ValueList getValues(ASeqCompSeqExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getFirst(), ctxt);
		list.addAll(ASetBindAssistantInterpreter.getValues(exp.getSetBind(),ctxt));

		if (exp.getPredicate() != null)
		{
			list.addAll( PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		}

		return list;
	}

	public static PExp findExpression(ASeqCompSeqExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getFirst(),lineno);
		if (found != null) return found;

		return exp.getPredicate() == null ? null : PExpAssistantInterpreter.findExpression(exp.getPredicate(),lineno);
	}

}
