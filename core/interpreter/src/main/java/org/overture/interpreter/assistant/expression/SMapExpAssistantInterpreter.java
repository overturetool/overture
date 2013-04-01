package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.SMapExpAssistantTC;

public class SMapExpAssistantInterpreter extends SMapExpAssistantTC
{

	public static ValueList getValues(SMapExp exp, ObjectContext ctxt)
	{
		switch (exp.kindSMapExp())
		{
			case AMapCompMapExp.kindSMapExp:
				return AMapCompMapExpAssistantInterpreter.getValues((AMapCompMapExp)exp,ctxt);
			case AMapEnumMapExp.kindSMapExp:
				return AMapEnumMapExpAssistantInterpreter.getValues((AMapEnumMapExp)exp,ctxt);
			default:
				return new ValueList();
		}
	}

	public static PExp findExpression(SMapExp exp, int lineno)
	{
		switch (exp.kindSMapExp())
		{
			case AMapCompMapExp.kindSMapExp:
				return AMapCompMapExpAssistantInterpreter.findExpression((AMapCompMapExp)exp,lineno);
			case AMapEnumMapExp.kindSMapExp:
				return AMapEnumMapExpAssistantInterpreter.findExpression((AMapEnumMapExp)exp,lineno);
			default:
				return null;
		}
	}

}
