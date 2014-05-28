package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.traces.AInstanceTraceDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.traces.AlternativeTraceNode;
import org.overture.interpreter.traces.RepeatTraceNode;
import org.overture.interpreter.traces.StatementTraceNode;
import org.overture.interpreter.traces.TraceNode;
import org.overture.interpreter.traces.TraceVariableList;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;

/***************************************
 * 
 * This method expands the trace of a definition term in the tree. 
 * 
 * @author gkanos
 *
 ****************************************/
public class TermTraceExpander extends QuestionAnswerAdaptor<Context, TraceNode>
{
	protected IInterpreterAssistantFactory af;
	
	public TermTraceExpander(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public TraceNode caseAInstanceTraceDefinition(
			AInstanceTraceDefinition term, Context ctxt)
			throws AnalysisException
	{
		assert false : "this one is not in Nicks tree";
		return null;
	}
	
	@Override
	public TraceNode caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition term, Context ctxt)
			throws AnalysisException
	{
		AlternativeTraceNode node = new AlternativeTraceNode();

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb : term.getDef().getBindings())
			{
				ValueList bvals = af.createPMultipleBindAssistant().getBindValues(mb, ctxt);

				for (PPattern p : mb.getPlist())
				{
					Quantifier q = new Quantifier(p, bvals);
					quantifiers.add(q);
				}
			}

			quantifiers.init(ctxt, true);

			if (quantifiers.finished()) // No entries at all
			{
				node.alternatives.add(new StatementTraceNode(AstFactory.newASkipStm(term.getLocation())));
				return node;
			}

			while (quantifiers.hasNext())
			{
				Context evalContext = new Context(af, term.getLocation(), "TRACE", ctxt);
				NameValuePairList nvpl = quantifiers.next();
				boolean matches = true;

				for (NameValuePair nvp : nvpl)
				{
					Value v = evalContext.get(nvp.name);

					if (v == null)
					{
						evalContext.put(nvp.name, nvp.value);
					} else
					{
						if (!v.equals(nvp.value))
						{
							matches = false;
							break; // This quantifier set does not match
						}
					}
				}

				if (matches
						&& (term.getStexp() == null || term.getStexp().apply(VdmRuntime.getExpressionEvaluator(), evalContext).boolValue(ctxt)))
				{
					TraceNode exp = af.createPTraceDefinitionAssistant().expand(term.getBody(), evalContext);
					exp.addVariables(new TraceVariableList(evalContext, af.createPDefinitionAssistant().getDefinitions(term.getDef())));
					node.alternatives.add(exp);
				}
			}
		} catch (AnalysisException e)
		{
			if (e instanceof ValueException)
			{
				throw new ContextException((ValueException) e, term.getLocation());
			}
		}

		return node;
	}
	
	@Override
	public TraceNode caseALetDefBindingTraceDefinition(
			ALetDefBindingTraceDefinition term, Context ctxt)
			throws AnalysisException
	{
		Context evalContext = new Context(af, term.getLocation(), "TRACE", ctxt);

		for (PDefinition d : term.getLocalDefs())
		{
			evalContext.putList(af.createPDefinitionAssistant().getNamedValues(d, evalContext));
		}

		TraceNode node = af.createPTraceDefinitionAssistant().expand(term.getBody(), evalContext);
		node.addVariables(new TraceVariableList(evalContext, term.getLocalDefs()));
		return node;
	}
	
	@Override
	public TraceNode caseARepeatTraceDefinition(ARepeatTraceDefinition term,
			Context ctxt) throws AnalysisException
	{
		TraceNode body = af.createPTraceCoreDefinitionAssistant().expand(term.getCore(), ctxt);

		if (term.getFrom() == 1 && term.getTo() == 1)
		{
			return body;
		} else
		{
			return new RepeatTraceNode(body, term.getFrom(), term.getTo());
		}
	}
	
	@Override
	public TraceNode defaultPTraceDefinition(PTraceDefinition term,
			Context ctxt) throws AnalysisException
	{
		return null;
	}
	
	@Override
	public TraceNode createNewReturnValue(INode node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TraceNode createNewReturnValue(Object node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
