package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ASeqEnumSeqExpAssistantInterpreter 
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASeqEnumSeqExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getValues(ASeqEnumSeqExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getMembers(), ctxt);
	}

	public static PExp findExpression(ASeqEnumSeqExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return PExpAssistantInterpreter.findExpression(exp.getMembers(), lineno);
	}

}
