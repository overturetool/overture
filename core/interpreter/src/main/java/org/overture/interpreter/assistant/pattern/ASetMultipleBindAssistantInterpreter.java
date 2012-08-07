package org.overture.interpreter.assistant.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;
import org.overture.typechecker.assistant.pattern.ASetMultipleBindAssistantTC;

public class ASetMultipleBindAssistantInterpreter extends
		ASetMultipleBindAssistantTC
{

	public static ValueList getBindValues(ASetMultipleBind mb, Context ctxt)
	{
		try
		{
			ValueList vl = new ValueList();
			ValueSet vs = mb.getSet().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);
			vs.sort();

			for (Value v: vs)
			{
				v = v.deref();

				if (v instanceof SetValue)
				{
					SetValue sv = (SetValue)v;
					vl.addAll(sv.permutedSets());
				}
				else
				{
					vl.add(v);
				}
			}

			return vl;
		}
		catch (AnalysisException e)
		{
			if(e instanceof ValueException)
			{
				VdmRuntimeError.abort(mb.getLocation(),(ValueException) e);
			}
			return null;

		} 
	}

	public static ValueList getValues(ASetMultipleBind mb, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(mb.getSet(), ctxt);
		
	}

}
