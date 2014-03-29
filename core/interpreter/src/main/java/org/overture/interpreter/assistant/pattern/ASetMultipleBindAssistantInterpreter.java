package org.overture.interpreter.assistant.pattern;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ASetMultipleBindAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetMultipleBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getBindValues(ASetMultipleBind mb, Context ctxt)
//	{
//		try
//		{
//			ValueList vl = new ValueList();
//			ValueSet vs = mb.getSet().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
//			vs.sort();
//
//			for (Value v : vs)
//			{
//				v = v.deref();
//
//				if (v instanceof SetValue)
//				{
//					SetValue sv = (SetValue) v;
//					vl.addAll(sv.permutedSets());
//				} else
//				{
//					vl.add(v);
//				}
//			}
//
//			return vl;
//		} catch (AnalysisException e)
//		{
//			if (e instanceof ValueException)
//			{
//				VdmRuntimeError.abort(mb.getLocation(), (ValueException) e);
//			}
//			return null;
//
//		}
//	}

//	public static ValueList getValues(ASetMultipleBind mb, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(mb.getSet(), ctxt);
//
//	}

}
