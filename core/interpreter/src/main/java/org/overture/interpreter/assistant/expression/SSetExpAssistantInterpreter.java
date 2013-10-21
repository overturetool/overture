package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SSetExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class SSetExpAssistantInterpreter // extends SSetExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSetExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(SSetExp exp, ObjectContext ctxt)
	{
		if (exp instanceof ASetCompSetExp)
		{
			return ASetCompSetExpAssistantInterpreter.getValues((ASetCompSetExp) exp, ctxt);
		} else if (exp instanceof ASetEnumSetExp)
		{
			return ASetEnumSetExpAssistantInterpreter.getValues((ASetEnumSetExp) exp, ctxt);
		} else
		{
			return new ValueList();
		}
	}

	public static PExp findExpression(SSetExp exp, int lineno)
	{
		if (exp instanceof ASetCompSetExp)
		{
			return ASetCompSetExpAssistantInterpreter.findExpression((ASetCompSetExp) exp, lineno);
		} else if (exp instanceof ASetEnumSetExp)
		{
			return ASetEnumSetExpAssistantInterpreter.findExpression((ASetEnumSetExp) exp, lineno);
		} else if (exp instanceof ASetRangeSetExp)
		{
			return ASetRangeSetExpAssistantInterpreter.findExpression((ASetRangeSetExp) exp, lineno);
		} else
		{
			return null;
		}
	}

}
