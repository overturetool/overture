package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ASetEnumSetExpAssistantTC;

public class ASetEnumSetExpAssistantInterpreter extends
		ASetEnumSetExpAssistantTC
{

	public static ValueList getValues(ASetEnumSetExp exp, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(exp.getMembers(),ctxt);
	}

}
