package org.overture.interpreter.assistant.pattern;

import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.ASetMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.ATypeMultipleBindAssistantTC;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class PMultipleBindAssistantInterpreter extends PMultipleBindAssistantTC
{

	public static ValueList getBindValues(PMultipleBind mb, Context ctxt)
	{
		switch (mb.kindPMultipleBind())
		{
			case SET:
				return ASetMultipleBindAssistantInterpreter.getBindValues((ASetMultipleBind) mb,ctxt);
			case TYPE:
				return ATypeMultipleBindAssistantInterpreter.getBindValues((ATypeMultipleBind)mb,ctxt);
			default:
				break;
		}
		return null;
	}

}
