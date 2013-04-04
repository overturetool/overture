package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.SSeqExpAssistantTC;

public class SSeqExpAssistantInterpreter extends SSeqExpAssistantTC
{

	public static ValueList getValues(SSeqExp exp, ObjectContext ctxt)
	{
		switch (exp.kindSSeqExp())
		{
			case ASeqCompSeqExp.kindSSeqExp:
				return ASeqCompSeqExpAssistantInterpreter.getValues((ASeqCompSeqExp)exp,ctxt);
			case ASeqEnumSeqExp.kindSSeqExp:
				return ASeqEnumSeqExpAssistantInterpreter.getValues((ASeqEnumSeqExp)exp,ctxt);
			default:
				return new ValueList();
		}
	}

	public static PExp findExpression(SSeqExp exp, int lineno)
	{
		switch (exp.kindSSeqExp())
		{
			case ASeqCompSeqExp.kindSSeqExp:
				return ASeqCompSeqExpAssistantInterpreter.findExpression((ASeqCompSeqExp)exp,lineno);
			case ASeqEnumSeqExp.kindSSeqExp:
				return ASeqEnumSeqExpAssistantInterpreter.findExpression((ASeqEnumSeqExp)exp,lineno);
			default:
				return null;
		}
	}

}
