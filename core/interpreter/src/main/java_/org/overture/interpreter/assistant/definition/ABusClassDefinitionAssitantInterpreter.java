package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.values.BUSValue;
import org.overture.interpreter.values.ValueSet;

public class ABusClassDefinitionAssitantInterpreter
{

	public static BUSValue makeVirtualBUS(ValueSet cpus)
	{
		return new BUSValue((AClassType)instance.getType(), cpus);
	}

}
