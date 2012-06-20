package org.overture.interpreter.assistant.type;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.SInvariantType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;

public class SInvariantTypeAssistantInterpreter
{

	public static  FunctionValue getInvariant(SInvariantType type, Context ctxt)
	{
		AExplicitFunctionDefinition invdef = type.getInvDef();
		if (invdef != null)
		{
			try
			{
				Value v = ctxt.getGlobal().lookup(invdef.getName());
				return v.functionValue(ctxt);
			}
			catch (ValueException e)
			{
				RuntimeError.abort(type.getLocation(),e);
			}
		}

		return null;
	}

	public static ValueList getAllValues(SInvariantType type, Context ctxt) throws ValueException
	{
		switch (type.kindSInvariantType())
		{
			case NAMED:
				return ANamedInvariantTypeAssistantInterpreter.getAllValues((ANamedInvariantType) type,ctxt);
			case RECORD:
				return ARecordInvariantTypeAssistantInterpreter.getAllValues((ARecordInvariantType)type,ctxt);
			default:
				throw new ValueException(4, "Cannot get bind values for type " + type, ctxt);
		}
	}

}
