package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AMapEnumMapExpAssistantInterpreter 
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapEnumMapExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(AMapEnumMapExp exp, ObjectContext ctxt)
//	{
//		ValueList list = new ValueList();
//
//		for (AMapletExp maplet : exp.getMembers())
//		{
//			list.addAll(AMapletExpAssistantInterpreter.getValues(maplet, ctxt));
//		}
//
//		return list;
//	}

	public static PExp findExpression(AMapEnumMapExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		for (AMapletExp m : exp.getMembers())
		{
			found = AMapletExpAssistantInterpreter.findExpression(m, lineno);
			if (found != null)
				return found;
		}

		return null;
	}

}
