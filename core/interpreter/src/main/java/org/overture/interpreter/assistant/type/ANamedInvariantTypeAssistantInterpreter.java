package org.overture.interpreter.assistant.type;

import org.overture.ast.types.ANamedInvariantType;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.InvariantValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;

public class ANamedInvariantTypeAssistantInterpreter extends
		ANamedInvariantTypeAssistantTC
{

	public static ValueList getAllValues(ANamedInvariantType type, Context ctxt) throws ValueException
	{
		ValueList raw = PTypeAssistantInterpreter.getAllValues(type.getType(), ctxt);
		boolean checks = Settings.invchecks;
		Settings.invchecks = true;

		ValueList result = new ValueList();
		for (Value v: raw)
		{
			try
			{
				result.add(new InvariantValue(type, v, ctxt));
			}
			catch (ValueException e)
			{
				// Raw value not in type because of invariant
			}
		}

		Settings.invchecks = checks;
		return result;
	}

}
