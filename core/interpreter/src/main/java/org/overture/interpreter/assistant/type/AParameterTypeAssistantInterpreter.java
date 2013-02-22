package org.overture.interpreter.assistant.type;

import org.overture.ast.types.AParameterType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ParameterValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.AParameterTypeAssistantTC;

public class AParameterTypeAssistantInterpreter extends AParameterTypeAssistantTC{

	public static ValueList getAllValues(AParameterType type, Context ctxt) throws ValueException
	{
		Value t = ctxt.lookup(type.getName());

		if (t == null)
		{
			throw new ValueException(4008, "No such type parameter @" + type.getName() + " in scope", ctxt);
		}
		else if (t instanceof ParameterValue)
		{
			ParameterValue tv = (ParameterValue)t;
			return PTypeAssistantInterpreter.getAllValues(tv.type, ctxt);
		}
		
		throw new ValueException(4009, "Type parameter/local variable name clash, @" + type.getName(), ctxt);
	}
	
}
