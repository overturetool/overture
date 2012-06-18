package org.overture.interpreter.assistant.type;

import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.SBasicType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;

public class SBasicTypeAssistantInterpreter
{

	public static ValueList getAllValues(SBasicType type, Context ctxt) throws ValueException
	{
		switch (type.kindSBasicType())
		{
			case BOOLEAN:
				return ABooleanBasicTypeAssistantInterpreter.getAllValues((ABooleanBasicType)type,ctxt);
			default:
				throw new ValueException(4, "Cannot get bind values for type " + type, ctxt);
		}
	}

}
