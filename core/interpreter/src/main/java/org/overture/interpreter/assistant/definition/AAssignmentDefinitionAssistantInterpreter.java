package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AAssignmentDefinitionAssistantInterpreter /*
														 * extends AAssignmentDefinitionAssistantTC
														 */
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AAssignmentDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		// super(af);
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(AAssignmentDefinition d,
//			Context initialContext)
//	{
//		try
//		{
//			Value v = d.getExpression().apply(VdmRuntime.getExpressionEvaluator(), initialContext);
//
//			if (!v.isUndefined())
//			{
//				v = v.convertTo(d.getType(), initialContext);
//			}
//
//			return new NameValuePairList(new NameValuePair(d.getName(), v.getUpdatable(null)));
//		} catch (AnalysisException e)
//		{
//			if (e instanceof ValueException)
//			{
//				VdmRuntimeError.abort(d.getLocation(), (ValueException) e);
//			}
//			return null;
//		}
//	}

	public static ValueList getValues(AAssignmentDefinition d,
			ObjectContext ctxt)
	{
		return PExpAssistantInterpreter.getValues(d.getExpression(), ctxt);
	}

	public static PExp findExpression(AAssignmentDefinition d, int lineno)
	{
		return PExpAssistantInterpreter.findExpression(d.getExpression(), lineno);
	}

}
