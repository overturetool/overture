package org.overture.interpreter.assistant.pattern;

import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;

public class ATypeMultipleBindAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATypeMultipleBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static ValueList getBindValues(ATypeMultipleBind mb, Context ctxt)
			throws ValueException
	{
		return PTypeAssistantInterpreter.getAllValues(mb.getType(), ctxt);
	}

	public static ValueList getValues(ATypeMultipleBind mb, ObjectContext ctxt)
	{
		return new ValueList();
	}

}
