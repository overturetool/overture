package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;

public class AAssignmentDefinitionAssistantInterpreter
{

	public static NameValuePairList getNamedValues(AAssignmentDefinition d,
			Context initialContext) throws Throwable
	{
		 try
	        {
		        Value v = d.getExpression().apply(VdmRuntime.getExpressionEvaluator(),initialContext);

		        if (!v.isUndefined())
		        {
		        	v = v.convertTo(d.getType(), initialContext);
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
