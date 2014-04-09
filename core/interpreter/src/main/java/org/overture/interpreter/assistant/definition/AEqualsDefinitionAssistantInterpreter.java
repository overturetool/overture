package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetBindAssistantInterpreter;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class AEqualsDefinitionAssistantInterpreter

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AEqualsDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static NameValuePairList getNamedValues(AEqualsDefinition d,
//			Context initialContext)
//	{
//		Value v;
//		try
//		{
//			v = d.getTest().apply(VdmRuntime.getExpressionEvaluator(), initialContext);
//		} catch (AnalysisException e1)
//		{
//			if (e1 instanceof ValueException)
//			{
//				VdmRuntimeError.abort(d.getLocation(), (ValueException) e1);
//			}
//			return null;
//		}
//
//		NameValuePairList nvpl = null;
//
//		if (d.getPattern() != null)
//		{
//			try
//			{
//				nvpl = PPatternAssistantInterpreter.getNamedValues(d.getPattern(), v, initialContext);
//			} catch (PatternMatchException e)
//			{
//				VdmRuntimeError.abort(e, initialContext);
//			}
//		} else if (d.getTypebind() != null)
//		{
//			try
//			{
//				Value converted = v.convertTo(d.getTypebind().getType(), initialContext);
//				nvpl = PPatternAssistantInterpreter.getNamedValues(d.getTypebind().getPattern(), converted, initialContext);
//			} catch (PatternMatchException e)
//			{
//				VdmRuntimeError.abort(e, initialContext);
//			} catch (ValueException e)
//			{
//				VdmRuntimeError.abort(d.getLocation(), e);
//			}
//		} else if (d.getSetbind() != null)
//		{
//			try
//			{
//				ValueSet set = d.getSetbind().getSet().apply(VdmRuntime.getExpressionEvaluator(), initialContext).setValue(initialContext);
//
//				if (!set.contains(v))
//				{
//					VdmRuntimeError.abort(d.getLocation(), 4002, "Expression value is not in set bind", initialContext);
//				}
//
//				nvpl = PPatternAssistantInterpreter.getNamedValues(d.getSetbind().getPattern(), v, initialContext);
//			} catch (AnalysisException e)
//			{
//				if (e instanceof PatternMatchException)
//				{
//					VdmRuntimeError.abort((PatternMatchException) e, initialContext);
//				}
//
//				if (e instanceof ValueException)
//				{
//					VdmRuntimeError.abort(d.getLocation(), (ValueException) e);
//				}
//			}
//		}
//
//		return nvpl;
//	}

//	public static ValueList getValues(AEqualsDefinition d, ObjectContext ctxt)
//	{
//		ValueList list = PExpAssistantInterpreter.getValues(d.getTest(), ctxt);
//
//		if (d.getSetbind() != null)
//		{
//			list.addAll(ASetBindAssistantInterpreter.getValues(d.getSetbind(), ctxt));
//		}
//
//		return list;
//	}

//	public static PExp findExpression(AEqualsDefinition d, int lineno)
//	{
//		return PExpAssistantInterpreter.findExpression(d.getTest(), lineno);
//	}

}
