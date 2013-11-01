package org.overture.interpreter.assistant.type;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.SInvariantType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;

public class SInvariantTypeAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SInvariantTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static FunctionValue getInvariant(SInvariantType type, Context ctxt)
	{
		AExplicitFunctionDefinition invdef = type.getInvDef();
		if (invdef != null)
		{
			try
			{
				Value v = ctxt.getGlobal().lookup(invdef.getName());
				return v.functionValue(ctxt);
			} catch (ValueException e)
			{
				VdmRuntimeError.abort(type.getLocation(), e);
			}
		}

		return null;
	}

	public static ValueList getAllValues(SInvariantType type, Context ctxt)
			throws ValueException
	{
		if (type instanceof ANamedInvariantType)
		{
			return ANamedInvariantTypeAssistantInterpreter.getAllValues((ANamedInvariantType) type, ctxt);
		} else if (type instanceof ARecordInvariantType)
		{
			return ARecordInvariantTypeAssistantInterpreter.getAllValues((ARecordInvariantType) type, ctxt);
		} else
		{
			throw new ValueException(4, "Cannot get bind values for type "
					+ type, ctxt);
		}
	}

}
