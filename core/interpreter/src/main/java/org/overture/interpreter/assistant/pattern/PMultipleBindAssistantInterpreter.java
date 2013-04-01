package org.overture.interpreter.assistant.pattern;


import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class PMultipleBindAssistantInterpreter extends PMultipleBindAssistantTC
{

	public static ValueList getBindValues(PMultipleBind mb, Context ctxt) throws ValueException
	{
		switch (mb.kindPMultipleBind())
		{
			case ASetMultipleBind.kindPMultipleBind:
				return ASetMultipleBindAssistantInterpreter.getBindValues((ASetMultipleBind) mb,ctxt);
			case ATypeMultipleBind.kindPMultipleBind:
				return ATypeMultipleBindAssistantInterpreter.getBindValues((ATypeMultipleBind)mb,ctxt);
			default:
				break;
		}
		return null;
	}

	public static ValueList getValues(PMultipleBind mb,
			ObjectContext ctxt)
	{
		switch (mb.kindPMultipleBind())
		{
			case ASetMultipleBind.kindPMultipleBind:
				return ASetMultipleBindAssistantInterpreter.getValues((ASetMultipleBind)mb,ctxt);
			case ATypeMultipleBind.kindPMultipleBind:
				return ATypeMultipleBindAssistantInterpreter.getValues((ATypeMultipleBind)mb, ctxt);
			default:
				return new ValueList();
		}
	}

}
