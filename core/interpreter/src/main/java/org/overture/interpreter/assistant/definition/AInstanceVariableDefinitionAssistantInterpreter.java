package org.overture.interpreter.assistant.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.definition.AInstanceVariableDefinitionAssistantTC;

public class AInstanceVariableDefinitionAssistantInterpreter extends
		AInstanceVariableDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(
			AInstanceVariableDefinition d, Context initialContext) 
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
	        } catch (AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} 
	}

	public static ValueList getValues(AInstanceVariableDefinition d,
			ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(d.getExpression(), ctxt);
	}

	public static PExp findExpression(AInstanceVariableDefinition d, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(d.getExpression(), lineno);
	}

}
