package org.overture.interpreter.assistant.type;

import org.overture.ast.types.AQuoteType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.QuoteValue;
import org.overture.interpreter.values.ValueList;

public class AQuoteTypeAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AQuoteTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getAllValues(AQuoteType type, Context ctxt)
//	{
//		ValueList v = new ValueList();
//		v.add(new QuoteValue(type.getValue().getValue()));
//		return v;
//	}

}
