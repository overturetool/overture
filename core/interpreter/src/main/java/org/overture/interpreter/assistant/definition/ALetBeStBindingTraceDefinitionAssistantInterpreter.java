package org.overture.interpreter.assistant.definition;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ALetBeStBindingTraceDefinitionAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALetBeStBindingTraceDefinitionAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static TraceNode expand(ALetBeStBindingTraceDefinition term,
//			Context ctxt)
//	{
//		AlternativeTraceNode node = new AlternativeTraceNode();
//
//		try
//		{
//			QuantifierList quantifiers = new QuantifierList();
//
//			for (PMultipleBind mb : term.getDef().getBindings())
//			{
//				ValueList bvals = PMultipleBindAssistantInterpreter.getBindValues(mb, ctxt);
//
//				for (PPattern p : mb.getPlist())
//				{
//					Quantifier q = new Quantifier(p, bvals);
//					quantifiers.add(q);
//				}
//			}
//
//			quantifiers.init(ctxt, true);
//
//			if (quantifiers.finished()) // No entries at all
//			{
//				node.alternatives.add(new StatementTraceNode(AstFactory.newASkipStm(term.getLocation())));
//				return node;
//			}
//
//			while (quantifiers.hasNext())
//			{
//				Context evalContext = new Context(af, term.getLocation(), "TRACE", ctxt);
//				NameValuePairList nvpl = quantifiers.next();
//				boolean matches = true;
//
//				for (NameValuePair nvp : nvpl)
//				{
//					Value v = evalContext.get(nvp.name);
//
//					if (v == null)
//					{
//						evalContext.put(nvp.name, nvp.value);
//					} else
//					{
//						if (!v.equals(nvp.value))
//						{
//							matches = false;
//							break; // This quantifier set does not match
//						}
//					}
//				}
//
//				if (matches
//						&& (term.getStexp() == null || term.getStexp().apply(VdmRuntime.getExpressionEvaluator(), evalContext).boolValue(ctxt)))
//				{
//					TraceNode exp = PTraceDefinitionAssistantInterpreter.expand(term.getBody(), evalContext);
//					exp.addVariables(new TraceVariableList(evalContext, af.createPDefinitionAssistant().getDefinitions(term.getDef())));
//					node.alternatives.add(exp);
//				}
//			}
//		} catch (AnalysisException e)
//		{
//			if (e instanceof ValueException)
//			{
//				throw new ContextException((ValueException) e, term.getLocation());
//			}
//		}
//
//		return node;
//	}

}
