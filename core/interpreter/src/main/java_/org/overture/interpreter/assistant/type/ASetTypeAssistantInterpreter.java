package org.overture.interpreter.assistant.type;

import java.util.List;

import org.overture.ast.types.ASetType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;
import org.overture.typechecker.assistant.type.ASetTypeAssistantTC;

public class ASetTypeAssistantInterpreter extends ASetTypeAssistantTC
{

	public static ValueList getAllValues(ASetType type, Context ctxt) throws ValueException
	{
		ValueList list = PTypeAssistantInterpreter.getAllValues(type.getSetof(),ctxt);
		ValueSet set = new ValueSet(list.size());
		set.addAll(list);
		List<ValueSet> psets = set.powerSet();
		list.clear();

		for (ValueSet v: psets)
		{
			list.add(new SetValue(v));
		}

		return list;
	}
	
}
