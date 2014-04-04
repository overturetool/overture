package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AValueDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AValueDefinitionAssistantInterpreter

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AValueDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		//super(af);
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(AValueDefinition d,
//			Context initialContext)
//	{
//		Value v = null;
//
//		try
//		{
//			// UpdatableValues are constantized as they cannot be updated.
//			v = d.getExpression().apply(VdmRuntime.getExpressionEvaluator(), initialContext).convertTo(af.createPDefinitionAssistant().getType(d), initialContext).getConstant();
//			return PPatternAssistantInterpreter.getNamedValues(d.getPattern(), v, initialContext);
//		} catch (ValueException e)
//		{
//			VdmRuntimeError.abort(d.getLocation(), e);
//		} catch (PatternMatchException e)
//		{
//			VdmRuntimeError.abort(e, initialContext);
//		} catch (AnalysisException e)
//		{
//
//		}
//
//		return null;
//	}

//	public static ValueList getValues(AValueDefinition d, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(d.getExpression(), ctxt);
//	}

//	public static PExp findExpression(AValueDefinition d, int lineno)
//	{
//		return PExpAssistantInterpreter.findExpression(d.getExpression(), lineno);
//	}

}
