package org.overture.interpreter.assistant.pattern;


import org.overture.ast.patterns.ASetBind;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;
import org.overture.typechecker.assistant.pattern.ASetBindAssistantTC;

public class ASetBindAssistantInterpreter extends ASetBindAssistantTC
{

	public static ValueList getBindValues(ASetBind bind, Context ctxt) 
	{
		try
		{
			ValueList results = new ValueList();
			ValueSet elements =  bind.getSet().apply(VdmRuntime.getExpressionEvaluator(),ctxt).setValue(ctxt);
			elements.sort();

			for (Value e: elements)
			{
				e = e.deref();

				if (e instanceof SetValue)
				{
					SetValue sv = (SetValue)e;
					results.addAll(sv.permutedSets());
				}
				else
				{
					results.add(e);
				}
			}

			return results;
		}
		catch (ValueException ex)
		{
			RuntimeError.abort(bind.getLocation(),ex);
			return null;
		} catch (Throwable ex)
		{
			return null;
		}
	}

	public static ValueList getValues(ASetBind setBind,
			ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(setBind.getSet(),ctxt);
	}

}
