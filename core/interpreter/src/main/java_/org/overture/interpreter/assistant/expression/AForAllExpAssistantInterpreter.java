package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AForAllExpAssistantTC;

public class AForAllExpAssistantInterpreter extends AForAllExpAssistantTC
{

	public static ValueList getValues(AForAllExp exp, ObjectContext ctxt)
	{
		ValueList list = new ValueList();

		for (PMultipleBind mb: exp.getBindList())
		{
			list.addAll(PMultipleBindAssistantInterpreter.getValues(mb, ctxt));
		}

		list.addAll(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		return list;
	}

	public static PExp findExpression(AForAllExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpression(exp,lineno);
		if (found != null) return found;

		return PExpAssistantInterpreter.findExpression(exp.getPredicate(),lineno);
	}

}
