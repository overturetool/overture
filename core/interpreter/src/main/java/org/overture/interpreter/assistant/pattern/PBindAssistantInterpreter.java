package org.overture.interpreter.assistant.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.PBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;

public class PBindAssistantInterpreter extends PBindAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public ValueList getBindValues(PBind bind, Context ctxt)
			throws AnalysisException
	{
		
		return bind.apply(af.getSingleBindValuesCollector(), ctxt);// FIXME: should we handle exceptions like this
		
//		if (bind instanceof ASetBind)
//		{
//			return ASetBindAssistantInterpreter.getBindValues((ASetBind) bind, ctxt);
//		} else if (bind instanceof ATypeBind)
//		{
//			return ATypeBindAssistantInterpreter.getBindValues((ATypeBind) bind, ctxt);
//		} else
//		{
//			assert false : "Should not happen";
//			return null;
//		}
	}

	public static ValueList getValues(PBind bind, ObjectContext ctxt)
	{	
		
		try
		{
			return bind.apply(af.getBindValueCollector(),ctxt);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new ValueList();
		}
//		if (bind instanceof ASetBind)
//		{
//			return ASetBindAssistantInterpreter.getValues((ASetBind) bind, ctxt);
//		} else if (bind instanceof ATypeBind)
//		{
//			return ATypeBindAssistantInterpreter.getValues((ATypeBind) bind, ctxt);
//		} else
//		{
//			
//		}
	}

}
