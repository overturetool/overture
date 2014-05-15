package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AMapCompMapExpAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapCompMapExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(AMapCompMapExp exp, ObjectContext ctxt)
//	{
//		ValueList list = PExpAssistantInterpreter.getValues(exp.getFirst(), ctxt);
//
//		for (PMultipleBind mb : exp.getBindings())
//		{
//			list.addAll(PMultipleBindAssistantInterpreter.getValues(mb, ctxt));
//		}
//
//		if (exp.getPredicate() != null)
//		{
//			list.addAll(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
//		}
//
//		return list;
//	}

//	public static PExp findExpression(AMapCompMapExp exp, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
//		if (found != null)
//			return found;
//
//		found = PExpAssistantInterpreter.findExpression(exp.getFirst(), lineno);
//		if (found != null)
//			return found;
//
//		return exp.getPredicate() == null ? null
//				: PExpAssistantInterpreter.findExpression(exp.getPredicate(), lineno);
//	}

}
