package org.overture.interpreter.assistant.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;

public class AValueDefinitionAssistantInterpreter extends
		AValueDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(AValueDefinition d,
			Context initialContext)
	{
		Value v = null;

		try
		{
			// UpdatableValues are constantized as they cannot be updated.
			v = d.getExpression().apply(VdmRuntime.getExpressionEvaluator(),initialContext).convertTo(d.getType(), initialContext).getConstant();
			return PPatternAssistantInterpreter.getNamedValues(d.getPattern(), v, initialContext);
     	}
	    catch (ValueException e)
     	{
     		VdmRuntimeError.abort(d.getLocation(),e);
     	}
		catch (PatternMatchException e)
		{
			VdmRuntimeError.abort(e, initialContext);
		} catch (AnalysisException e)
		{
			
		}

		return null;
	}

	public static ValueList getValues(AValueDefinition d, ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(d.getExpression(), ctxt);
	}

	public static PExp findExpression(AValueDefinition d, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(d.getExpression(), lineno);
	}

}
