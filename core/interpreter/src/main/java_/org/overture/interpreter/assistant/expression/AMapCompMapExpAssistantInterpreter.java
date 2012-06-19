package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.AMapCompMapExpAssistantTC;

public class AMapCompMapExpAssistantInterpreter extends
		AMapCompMapExpAssistantTC
{

	public static ValueList getValues(AMapCompMapExp exp, ObjectContext ctxt)
	{
		ValueList list = PExpAssistantInterpreter.getValues(exp.getFirst(), ctxt);

		for (PMultipleBind mb: exp.getBindings())
		{
			list.addAll(PMultipleBindAssistantInterpreter.getValues(mb, ctxt));
		}

		if (exp.getPredicate() != null)
		{
			list.addAll(PExpAssistantInterpreter.getValues(exp.getPredicate(), ctxt));
		}

		return list;
	}

}
