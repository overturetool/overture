package org.overture.interpreter.assistant.expression;

import java.util.List;

import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AElseIfExpAssistantTC;

public class AElseIfExpAssistantInterpreter extends AElseIfExpAssistantTC
{

	public static ValueList getValues(AElseIfExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getElseIf(), ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getThen(), ctxt));
		return list;
	}

	public static PExp findExpression(AElseIfExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getThen(),lineno);
	}

	public static List<PExp> getSubExpressions(AElseIfExp exp)
	{
		List<PExp> subs = PExpAssistantInterpreter.getSubExpressions(exp.getElseIf());
		subs.addAll(PExpAssistantInterpreter.getSubExpressions(exp.getThen()));
		subs.add(exp);
		return subs;
	}

}
