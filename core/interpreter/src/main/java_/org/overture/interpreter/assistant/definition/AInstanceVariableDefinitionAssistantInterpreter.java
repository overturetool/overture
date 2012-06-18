package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.definition.AInstanceVariableDefinitionAssistantTC;

public class AInstanceVariableDefinitionAssistantInterpreter extends
		AInstanceVariableDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(
			AInstanceVariableDefinition d, RootContext ctxt) throws Throwable
	{
		 try
	        {
		        Value v = d.getExpression().apply(VdmRuntime.getExpressionEvaluator(),ctxt);

		        if (!v.isUndefined())
		        {
		        	v = v.convertTo(d.getType(), ctxt);
		        }

				return new NameValuePairList(new NameValuePair(d.getName(), v.getUpdatable(null)));
	        }
	        catch (ValueException e)
	        {
	        	RuntimeError.abort(d.getLocation(),e);
	        	return null;
	        }
	}

}
