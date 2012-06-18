package org.overture.interpreter.assistant.type;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;

public class ARecordInvariantTypeAssistantInterpreter extends
		ARecordInvariantTypeAssistantTC
{

	public static ValueList getAllValues(ARecordInvariantType type, Context ctxt) throws ValueException
	{
		PTypeList types = new PTypeList();

		for (AFieldField f: type.getFields())
		{
			types.add(f.getType());
		}

		ValueList results = new ValueList(); 

		for (Value v: PTypeListAssistant.getAllValues(types,ctxt))
		{
			try
			{ 
				TupleValue tuple = (TupleValue)v;
				results.add(new RecordValue(type, tuple.values, ctxt));
			}
			catch (ValueException e)
			{
				// Value does not match invariant, so ignore it
			}
		}

		return results;
	}

}
