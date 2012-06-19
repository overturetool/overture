package org.overture.interpreter.assistant.expression;


import org.overture.ast.expressions.ACaseAlternative;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.ACaseAlternativeAssistantTC;

public class ACaseAlternativeAssistantInterpreter extends
		ACaseAlternativeAssistantTC
{

	public static ValueList getValues(ACaseAlternative c,
			ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(c.getResult(),ctxt);
	}

}
