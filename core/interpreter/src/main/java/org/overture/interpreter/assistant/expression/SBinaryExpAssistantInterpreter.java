package org.overture.interpreter.assistant.expression;

import java.util.List;

import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.SBinaryExpAssistantTC;

public class SBinaryExpAssistantInterpreter extends SBinaryExpAssistantTC
{

	public static ValueList getValues(SBinaryExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getLeft(),ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getRight(),ctxt));
		return list;
	}

	public static PExp findExpression(SBinaryExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(exp.getLeft(),lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getRight(),lineno);
	}

	public static List<PExp> getSubExpressions(SBinaryExp exp)
	{
		List<PExp> subs = PExpAssistantInterpreter.getSubExpressions(exp.getLeft());
		subs.addAll( PExpAssistantInterpreter.getSubExpressions(exp.getRight()));
		subs.add(exp);
		return subs;
	}
}
