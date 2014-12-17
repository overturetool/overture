package org.overture.interpreter.assistant.type;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.types.SInvariantType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.Value;

public class SInvariantTypeAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public SInvariantTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public FunctionValue getInvariant(SInvariantType type, Context ctxt)
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

}
