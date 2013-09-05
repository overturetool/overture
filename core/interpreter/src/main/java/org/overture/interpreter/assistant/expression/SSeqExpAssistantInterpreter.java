package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class SSeqExpAssistantInterpreter // extends SSeqExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SSeqExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(SSeqExp exp, ObjectContext ctxt)
	{
		if (exp instanceof ASeqCompSeqExp)
		{
			return ASeqCompSeqExpAssistantInterpreter.getValues((ASeqCompSeqExp) exp, ctxt);
		} else if (exp instanceof ASeqEnumSeqExp)
		{
			return ASeqEnumSeqExpAssistantInterpreter.getValues((ASeqEnumSeqExp) exp, ctxt);
		} else
		{
			return new ValueList();
		}
	}

	public static PExp findExpression(SSeqExp exp, int lineno)
	{
		if (exp instanceof ASeqCompSeqExp)
		{
			return ASeqCompSeqExpAssistantInterpreter.findExpression((ASeqCompSeqExp) exp, lineno);
		} else if (exp instanceof ASeqEnumSeqExp)
		{
			return ASeqEnumSeqExpAssistantInterpreter.findExpression((ASeqEnumSeqExp) exp, lineno);
		} else
		{
			return null;
		}
	}

}
