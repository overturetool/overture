package org.overture.interpreter.types.assistant;

import org.overture.interpreter.ast.types.AFieldFieldInterpreter;
import org.overture.interpreter.ast.types.ARecordInvariantTypeInterpreter;


public class ARecordInvariantTypeInterpreterAssistant
{

	public static AFieldFieldInterpreter findField(
			ARecordInvariantTypeInterpreter type, String tag)
	{
		for (AFieldFieldInterpreter f: type.getFields())
		{
			if (f.getTag().equals(tag))
			{
				return f;
			}
		}

		return null;
	}

}
