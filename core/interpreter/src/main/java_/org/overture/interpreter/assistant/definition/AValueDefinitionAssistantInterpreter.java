package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AValueDefinition;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;

public class AValueDefinitionAssistantInterpreter extends
		AValueDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(AValueDefinition d,
			RootContext ctxt) throws Throwable
	{
		Value v = null;

		try
		{
			// UpdatableValues are constantized as they cannot be updated.
			v = d.getExpression().apply(VdmRuntime.getExpressionEvaluator(),ctxt).convertTo(d.getType(), ctxt).getConstant();
			return PPatternAssistantInterpreter.getNamedValues(d.getPattern(), v, ctxt);
     	}
	    catch (ValueException e)
     	{
     		RuntimeError.abort(d.getLocation(),e);
     	}
		catch (PatternMatchException e)
		{
			RuntimeError.abort(e, ctxt);
		}

		return null;
	}

}
