package org.overture.interpreter.assistant.type;

import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.SBasicType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;

public class SBasicTypeAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SBasicTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getAllValues(SBasicType type, Context ctxt)
			throws ValueException
	{
		if (type instanceof ABooleanBasicType)
		{
			return ABooleanBasicTypeAssistantInterpreter.getAllValues((ABooleanBasicType) type, ctxt);
		} else
		{
			throw new ValueException(4, "Cannot get bind values for type "
					+ type, ctxt);
		}
	}

}
