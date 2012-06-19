package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ASeqEnumSeqExpAssistantTC;

public class ASeqEnumSeqExpAssistantInterpreter extends
		ASeqEnumSeqExpAssistantTC
{

	public static ValueList getValues(ASeqEnumSeqExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getMembers(), ctxt);
	}

}
