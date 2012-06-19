package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ASameBaseClassExpAssistantTC;

public class ASameClassExpAssistantInterpreter extends
		ASameBaseClassExpAssistantTC
{

	public static ValueList getValues(ASameBaseClassExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getLeft(), ctxt);
		list.addAll(PExpAssistantInterpreter.getValues(exp.getRight(), ctxt));
		return list;
	}

	public static PExp findExpression(ASameClassExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getLeft(),lineno);
		if (found != null) return found;

		found = PExpAssistantInterpreter.findExpression(exp.getRight(),lineno);
		if (found != null) return found;

		return null;
	}

}
