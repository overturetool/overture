package org.overture.interpreter.assistant.type;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ASetType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;

public class ASetTypeAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getAllValues(ASetType type, Context ctxt)
			throws AnalysisException
	{
		ValueList list = PTypeAssistantInterpreter.getAllValues(type.getSetof(), ctxt);
		ValueSet set = new ValueSet(list.size());
		set.addAll(list);
		List<ValueSet> psets = set.powerSet();
		list.clear();

		for (ValueSet v : psets)
		{
			list.add(new SetValue(v));
		}

		return list;
	}

}
