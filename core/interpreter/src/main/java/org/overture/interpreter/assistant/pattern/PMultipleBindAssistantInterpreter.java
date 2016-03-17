package org.overture.interpreter.assistant.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.eval.BindState;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.PMultipleBindAssistantTC;

public class PMultipleBindAssistantInterpreter extends PMultipleBindAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PMultipleBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public ValueList getBindValues(PMultipleBind mb, Context ctxt, boolean permuted)
			throws ValueException, AnalysisException
	{
		return mb.apply(af.getBindValuesCollector(), new BindState(ctxt, permuted));
	}

	public ValueList getValues(PMultipleBind mb, ObjectContext ctxt)
	{
		try
		{
			return mb.apply(af.getValueCollector(), ctxt);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new ValueList();
		}
	}

}
