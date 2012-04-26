package org.overture.interpreter.types.assistant;

import org.overture.interpreter.ast.types.SInvariantTypeInterpreter;
import org.overture.interpreter.types.assistant.PTypeInterpreterAssistant;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.Value;

public class SInvariantTypeInterpreterAssistant
{

	public static FunctionValue getInvariant(
			SInvariantTypeInterpreter type, Context ctxt)
	{
		if (type.getInvDef() != null)
		{
			try
			{
				Value v = ctxt.getGlobal().lookup(type.getInvDef().getName());
				return v.functionValue(ctxt);
			}
			catch (ValueException e)
			{
				PTypeInterpreterAssistant.abort(type, e);
			}
		}

		return null;
	}

}
