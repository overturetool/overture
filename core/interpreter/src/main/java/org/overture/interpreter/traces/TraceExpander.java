package org.overture.interpreter.traces;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AInstanceTraceDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceCoreDefinition;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.PStm;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;

/**
 * This method expands the trace of a core element in the tree.
 * 
 * @author pvj
 */
public class TraceExpander extends QuestionAnswerAdaptor<Context, TraceNode>
{
	protected IInterpreterAssistantFactory af;

	public TraceExpander(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public TraceNode caseAApplyExpressionTraceCoreDefinition(
			AApplyExpressionTraceCoreDefinition core, Context ctxt)
			throws AnalysisException
	{
		// return AApplyExpressionTraceCoreDefinitionAssistantInterpreter.expand(core, ctxt);
		List<PExp> newargs = new Vector<PExp>();
		List<PExp> args = null;

		if (core.getCallStatement() instanceof ACallStm)
		{
			ACallStm stmt = (ACallStm) core.getCallStatement();
			args = stmt.getArgs();
		} else
		{
			ACallObjectStm stmt = (ACallObjectStm) core.getCallStatement();
			args = stmt.getArgs();
		}

		for (PExp arg : args)
		{
			Value v = null;
			try
			{
				v = arg.apply(VdmRuntime.getExpressionEvaluator(), ctxt).deref();
			} catch (AnalysisException e1)
			{
				e1.printStackTrace();
			}

			if (v instanceof ObjectValue)
			{
				newargs.add(arg.clone());
			} else
			{
				String value = v.toString();
				
				// If the value includes mk_R() expressions, we may not be able to resolve the type,
				// either because the type R is not explicit (like mk_M`R) or because the type may
				// have been exported from its module as a "struct" and so mk_R(x) is not legal.
				// Note that a RecordValue's toString does not explicitly expand the module name.
				// So we exclude anything with mk_R expressions too...
				
				if (!value.matches("mk_token\\(.*\\)") && value.matches(".*mk_\\w+\\(.*"))
				{
					newargs.add(arg.clone()); // Give up!
				}
				else
				{
    				LexTokenReader ltr = new LexTokenReader(value, Settings.dialect, arg.getLocation());
    				ExpressionReader er = new ExpressionReader(ltr);
    				er.setCurrentModule(core.getCurrentModule());
    
    				try
    				{
    					newargs.add(er.readExpression());
    				} catch (ParserException e)
    				{
    					newargs.add(arg.clone()); // Give up!
    				} catch (LexException e)
    				{
    					newargs.add(arg.clone()); // Give up!
    				}
				}
			}
		}

		PStm newStatement = null;

		if (core.getCallStatement() instanceof ACallStm)
		{
			ACallStm stmt = (ACallStm) core.getCallStatement();
			newStatement = AstFactory.newACallStm(stmt.getName().clone(), newargs);
		} else
		{
			ACallObjectStm stmt = (ACallObjectStm) core.getCallStatement();
			ACallObjectStm newCallStatement;

			if (stmt.getClassname() != null)
			{
				newCallStatement = AstFactory.newACallObjectStm(stmt.getDesignator().clone(), stmt.getClassname().clone(), newargs);
			} else
			{
				newCallStatement = AstFactory.newACallObjectStm(stmt.getDesignator().clone(), (LexIdentifierToken) stmt.getFieldname().clone(), newargs);
			}
			
			if(stmt.getField() != null)
			{
				newCallStatement.setField(stmt.getField().clone());
			}
			
			newStatement = newCallStatement;
		}

		return new StatementTraceNode(newStatement);
	}

	@Override
	public TraceNode caseABracketedExpressionTraceCoreDefinition(
			ABracketedExpressionTraceCoreDefinition core, Context ctxt)
			throws AnalysisException
	{
		// return ABracketedExpressionTraceCoreDefinitionAssitantInterpreter.expand(core, ctxt);
		SequenceTraceNode node = new SequenceTraceNode();

		for (ATraceDefinitionTerm term : core.getTerms())
		{
			// node.nodes.add(ATraceDefinitionTermAssistantInterpreter.expand(term, ctxt));
			node.nodes.add(term.apply(THIS, ctxt));
		}

		return node;
	}

	@Override
	public TraceNode caseAConcurrentExpressionTraceCoreDefinition(
			AConcurrentExpressionTraceCoreDefinition core, Context ctxt)
			throws AnalysisException
	{
		// return AConcurrentExpressionTraceCoreDefinitionAssistantInterpreter.expand(core, ctxt);
		ConcurrentTraceNode node = new ConcurrentTraceNode();

		for (PTraceDefinition term : core.getDefs())
		{
			// node.nodes.add(PTraceDefinitionAssistantInterpreter.expand(term, ctxt));
			node.nodes.add(term.apply(THIS, ctxt));
		}

		return node;
	}

	@Override
	public TraceNode caseATraceDefinitionTerm(ATraceDefinitionTerm node,
			Context question) throws AnalysisException
	{
		AlternativeTraceNode newNode = new AlternativeTraceNode();

		for (PTraceDefinition term : node.getList())
		{
			newNode.alternatives.add(term.apply(THIS, question));
			// newNode.alternatives.add(PTraceDefinitionAssistantInterpreter.expand(term, ctxt));
		}

		return newNode;
	}

	@Override
	public TraceNode defaultPTraceCoreDefinition(PTraceCoreDefinition node,
			Context question) throws AnalysisException
	{
		assert false : "Should not happen";
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
		// return ALetBeStBindingTraceDefinitionAssistantInterpreter.expand(term, ctxt);
		AlternativeTraceNode node = new AlternativeTraceNode();

		try
		{
			QuantifierList quantifiers = new QuantifierList();

			for (PMultipleBind mb : term.getDef().getBindings())
			{
				ValueList bvals = af.createPMultipleBindAssistant().getBindValues(mb, ctxt, true);	// NB permuted

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
					TraceNode exp = term.getBody().apply(THIS, evalContext);
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
		// return ALetDefBindingTraceDefinitionAssistantInterpreter.expand(term, ctxt);
		Context evalContext = new Context(af, term.getLocation(), "TRACE", ctxt);

		for (PDefinition d : term.getLocalDefs())
		{
			evalContext.putList(af.createPDefinitionAssistant().getNamedValues(d, evalContext));
		}

		TraceNode node = term.getBody().apply(THIS, evalContext);
		node.addVariables(new TraceVariableList(evalContext, term.getLocalDefs()));
		return node;
	}

	@Override
	public TraceNode caseARepeatTraceDefinition(ARepeatTraceDefinition term,
			Context ctxt) throws AnalysisException
	{
		TraceNode body = term.getCore().apply(af.getTraceExpander(),ctxt);
		//expand(term.getCore(), ctxt);

		if (term.getFrom() == 1 && term.getTo() == 1)
		{
			return body;
		} else
		{
			return new RepeatTraceNode(body, term.getFrom(), term.getTo());
		}
	}
	
//		public TraceNode expand(PTraceCoreDefinition core, Context ctxt)
//		{
//			try
//			{
//				return core.apply(af.getTraceExpander(), ctxt);
//			} catch (AnalysisException e)
//			{
//				return null;
//			}
//		}

}
