package org.overture.interpreter.assistant.pattern;

import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class PMultipleBindAssistantInterpreter extends PMultipleBindAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PMultipleBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static ValueList getBindValues(PMultipleBind mb, Context ctxt)
			throws ValueException
	{
		if (mb instanceof ASetMultipleBind) {
			return ASetMultipleBindAssistantInterpreter.getBindValues((ASetMultipleBind) mb, ctxt);
		} else if (mb instanceof ATypeMultipleBind) {
			return ATypeMultipleBindAssistantInterpreter.getBindValues((ATypeMultipleBind) mb, ctxt);
		} else {
		}
		return null;
	}

	public static ValueList getValues(PMultipleBind mb, ObjectContext ctxt)
	{
		if (mb instanceof ASetMultipleBind) {
			return ASetMultipleBindAssistantInterpreter.getValues((ASetMultipleBind) mb, ctxt);
		} else if (mb instanceof ATypeMultipleBind) {
			return ATypeMultipleBindAssistantInterpreter.getValues((ATypeMultipleBind) mb, ctxt);
		} else {
			return new ValueList();
		}
	}

}
