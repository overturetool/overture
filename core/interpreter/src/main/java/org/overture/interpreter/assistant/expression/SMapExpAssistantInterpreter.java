package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class SMapExpAssistantInterpreter // extends SMapExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SMapExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(SMapExp exp, ObjectContext ctxt)
	{
		if (exp instanceof AMapCompMapExp)
		{
			return AMapCompMapExpAssistantInterpreter.getValues((AMapCompMapExp) exp, ctxt);
		} else if (exp instanceof AMapEnumMapExp)
		{
			return AMapEnumMapExpAssistantInterpreter.getValues((AMapEnumMapExp) exp, ctxt);
		} else
		{
			return new ValueList();
		}
	}

	public static PExp findExpression(SMapExp exp, int lineno)
	{
		if (exp instanceof AMapCompMapExp)
		{
			return AMapCompMapExpAssistantInterpreter.findExpression((AMapCompMapExp) exp, lineno);
		} else if (exp instanceof AMapEnumMapExp)
		{
			return AMapEnumMapExpAssistantInterpreter.findExpression((AMapEnumMapExp) exp, lineno);
		} else
		{
			return null;
		}
	}

}
