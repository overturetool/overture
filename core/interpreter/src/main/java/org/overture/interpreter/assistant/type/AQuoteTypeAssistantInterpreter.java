package org.overture.interpreter.assistant.type;

import org.overture.ast.types.AQuoteType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.QuoteValue;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.AQuoteTypeAssistantTC;

public class AQuoteTypeAssistantInterpreter extends AQuoteTypeAssistantTC
{

	public static ValueList getAllValues(AQuoteType type, Context ctxt)
	{
		ValueList v = new ValueList();
		v.add(new QuoteValue(type.getValue().getValue()));
		return v;
	}

}
