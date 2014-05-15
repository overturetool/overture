package org.overture.interpreter.assistant.pattern;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ATypeMultipleBindAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATypeMultipleBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getBindValues(ATypeMultipleBind mb, Context ctxt)
//			throws ValueException
//	{
//		return PTypeAssistantInterpreter.getAllValues(mb.getType(), ctxt);
//	}

//	public static ValueList getValues(ATypeMultipleBind mb, ObjectContext ctxt)
//	{
//		return new ValueList();
//	}

}
