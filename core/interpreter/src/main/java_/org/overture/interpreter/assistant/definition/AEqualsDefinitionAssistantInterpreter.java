package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueSet;

public class AEqualsDefinitionAssistantInterpreter
{

	public static NameValuePairList getNamedValues(AEqualsDefinition d,
			RootContext ctxt) throws Throwable
	{
		Value v = d.getTest().apply(VdmRuntime.getExpressionEvaluator(),ctxt);
		NameValuePairList nvpl = null;

		if (d.getPattern() != null)
		{
			try
			{
				nvpl = PPatternAssistantInterpreter.getNamedValues(d.getPattern(),v, ctxt);
			}
			catch (PatternMatchException e)
			{
				RuntimeError.abort(e, ctxt);
			}
		}
		else if (d.getTypebind() != null)
		{
			try
			{
				Value converted = v.convertTo(d.getTypebind().getType(), ctxt);
				nvpl = PPatternAssistantInterpreter.getNamedValues(d.getTypebind().getPattern(),converted, ctxt);
			}
			catch (PatternMatchException e)
			{
				RuntimeError.abort(e, ctxt);
			}
			catch (ValueException e)
			{
				RuntimeError.abort(d.getLocation(),e);
			}
		}
		else if (d.getSetbind() != null)
		{
			try
			{
				ValueSet set = d.getSetbind().getSet().apply(VdmRuntime.getExpressionEvaluator(), ctxt).setValue(ctxt);

				if (!set.contains(v))
				{
					RuntimeError.abort(d.getLocation(),4002, "Expression value is not in set bind", ctxt);
				}

				nvpl = PPatternAssistantInterpreter.getNamedValues(d.getSetbind().getPattern() ,v, ctxt);
			}
			catch (PatternMatchException e)
			{
				RuntimeError.abort(e, ctxt);
			}
			catch (ValueException e)
			{
				RuntimeError.abort(d.getLocation(),e);
			}
		}

		return nvpl;
	}

}
