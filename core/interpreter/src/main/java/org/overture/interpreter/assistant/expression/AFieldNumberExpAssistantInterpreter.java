package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AFieldNumberExpAssistantInterpreter // extends
// AFieldNumberExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AFieldNumberExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(AFieldNumberExp exp, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(exp.getTuple(), ctxt);
//	}

//	public static PExp findExpression(AFieldNumberExp exp, int lineno)
//	{
//		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
//		if (found != null)
//			return found;
//
//		return PExpAssistantInterpreter.findExpression(exp.getTuple(), lineno);
//	}

}
