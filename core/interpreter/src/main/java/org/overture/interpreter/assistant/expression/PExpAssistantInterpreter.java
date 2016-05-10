package org.overture.interpreter.assistant.expression;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameList;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class PExpAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		//super(af);
		this.af = af;
	}

	/**
	 * Return a list of all the updatable values read by the expression. This is used to add listeners to values that
	 * affect permission guards, so that the guard may be efficiently re-evaluated when the values change.
	 * 
	 * @param exp
	 * @param ctxt
	 *            The context in which to search for values.
	 * @return A list of values read by the expression.
	 */
	public ValueList getValues(PExp exp, ObjectContext ctxt)
	{
		try
		{
			return exp.apply(af.getExpressionValueCollector(), ctxt);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null; // Most have none
		}
	}

	/**
	 * Find an expression starting on the given line. Single expressions just compare their location to lineno, but
	 * expressions with sub-expressions iterate over their branches.
	 * 
	 * @param exp
	 * @param lineno
	 *            The line number to locate.
	 * @return An expression starting on the line, or null.
	 */
	public PExp findExpression(PExp exp, int lineno)
	{
		try
		{
			return exp.apply(af.getExpExpressionFinder(), lineno);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null; // Most have none
		}
	}

	public List<PExp> getSubExpressions(PExp exp)
	{
		try
		{
			return exp.apply(af.getSubExpressionsLocator());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			List<PExp> subs = new ArrayList<PExp>();
			subs.add(exp);
			return subs;
		}

	}

	public ValueList getValues(LinkedList<PExp> args, ObjectContext ctxt)
	{
		ValueList list = new ValueList();

		for (PExp exp : args)
		{
			list.addAll(af.createPExpAssistant().getValues(exp, ctxt));
		}

		return list;
	}

	public PExp findExpression(LinkedList<PExp> args, int lineno)
	{
		for (PExp exp : args)
		{
			PExp found = af.createPExpAssistant().findExpression(exp, lineno);
			if (found != null)
			{
				return found;
			}
		}

		return null;
	}

	public LexNameList getOldNames(PExp expression)
	{
		try
		{
			return expression.apply(af.getOldNameCollector());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return new LexNameList();
		}
	}

	public LexNameList getOldNames(LinkedList<PExp> args)
	{
		LexNameList list = new LexNameList();

		for (PExp exp : args)
		{
			list.addAll(getOldNames(exp));
		}

		return list;
	}

}
