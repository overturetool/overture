package org.overture.interpreter.assistant.type;

import org.overture.ast.types.AInMapMapType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.AInMapMapTypeAssistantTC;

public class AInMapMapTypeAssistantInterpreter extends AInMapMapTypeAssistantTC
{

	public static ValueList getAllValues(AInMapMapType type, Context ctxt) throws ValueException
	{
		ValueList maps = SMapTypeAssistantInterpreter.getAllValues(type, ctxt);
		ValueList result = new ValueList();
		
		for (Value map: maps)
		{
			MapValue vm = (MapValue)map;
			
			if (vm.values.isInjective())
			{
				result.add(vm);
			}
		}
		
		return result;
	}
	
}
