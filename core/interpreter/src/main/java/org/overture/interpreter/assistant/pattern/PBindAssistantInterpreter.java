package org.overture.interpreter.assistant.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.patterns.PBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.eval.BindState;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;

public class PBindAssistantInterpreter extends PBindAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public ValueList getBindValues(PBind bind, Context ctxt, boolean permuted)
			throws AnalysisException
	{

		return bind.apply(af.getSingleBindValuesCollector(), new BindState(ctxt, permuted));

	}

	public ValueList getValues(PBind bind, ObjectContext ctxt)
	{

		try
		{
			return bind.apply(af.getBindValueCollector(), ctxt);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new ValueList();
		}
	}

}
