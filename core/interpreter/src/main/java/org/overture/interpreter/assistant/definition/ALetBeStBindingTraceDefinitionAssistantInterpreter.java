package org.overture.interpreter.assistant.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.pattern.PMultipleBindAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.traces.AlternativeTraceNode;
import org.overture.interpreter.traces.StatementTraceNode;
import org.overture.interpreter.traces.TraceNode;
import org.overture.interpreter.traces.TraceVariableList;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.definition.AMultiBindListDefinitionAssistantTC;

public class ALetBeStBindingTraceDefinitionAssistantInterpreter
{

	public static TraceNode expand(ALetBeStBindingTraceDefinition term,
			Context ctxt)
	{
		AlternativeTraceNode node = new AlternativeTraceNode();

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb: term.getDef().getBindings())
			{
				ValueList bvals = PMultipleBindAssistantInterpreter.getBindValues(mb, ctxt);

				for (PPattern p: mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init();

			if (quantifiers.finished())		// No entries at all
			{
				node.alternatives.add(new StatementTraceNode(AstFactory.newASkipStm(term.getLocation())));
				return node;
			}

			while (quantifiers.hasNext(ctxt))
			{
				Context evalContext = new Context(term.getLocation(), "TRACE", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp: nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					}
					else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break;	// This quantifier set does not match
						}
					}
				}

				if (matches &&
					(term.getStexp() == null || term.getStexp().apply(VdmRuntime.getExpressionEvaluator(), evalContext).boolValue(ctxt)))
				{
					TraceNode exp = PTraceDefinitionAssistantInterpreter.expand(term.getBody(),evalContext);
					exp.addVariables(new TraceVariableList(evalContext, AMultiBindListDefinitionAssistantTC.getDefinitions(term.getDef())));
					node.alternatives.add(exp);
				}
			}
		}
        catch (AnalysisException e)
        {
        	if(e instanceof ValueException)
			{
        		throw new ContextException((ValueException) e, term.getLocation());
			}        	
		}

		return node;
	}

}
