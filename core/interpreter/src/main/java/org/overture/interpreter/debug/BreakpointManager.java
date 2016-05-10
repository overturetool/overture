package org.overture.interpreter.debug;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.ast.expressions.BreakpointExpression;
import org.overture.interpreter.runtime.Breakpoint;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.values.Value;

public class BreakpointManager
{

	static final Map<PExp, Breakpoint> expressionMap = new HashMap<PExp, Breakpoint>();
	static final Map<PStm, Breakpoint> statementMap = new HashMap<PStm, Breakpoint>();

	private BreakpointManager() {
	}

	public static Breakpoint getBreakpoint(INode node) throws AnalysisException
	{
		if(node instanceof PExp)
		{
			return getBreakpoint((PExp)node);
		}else if(node instanceof PStm)
		{
			return getBreakpoint((PStm)node);
		}
		throw new AnalysisException("Unsupported breakpoint node");
	}

	public static Breakpoint getBreakpoint(PExp exp)
	{
		if (!expressionMap.containsKey(exp))
		{
			expressionMap.put(exp, new Breakpoint(exp.getLocation()));
		}
		return expressionMap.get(exp);
	}

	public static Breakpoint getBreakpoint(PStm stmt)
	{
		if (!statementMap.containsKey(stmt))
		{
			statementMap.put(stmt, new Breakpoint(stmt.getLocation()));
		}
		return statementMap.get(stmt);
	}

	public static void setBreakpoint(PStm stmt, Breakpoint breakpoint)
	{
		statementMap.put(stmt, breakpoint);
	}

	public static void setBreakpoint(PExp exp, Breakpoint breakpoint)
	{
		expressionMap.put(exp, breakpoint);
	}

	public static boolean shouldStop(PExp exp, Context ctxt)
			throws ValueException
	{
		return evalBreakpointCondition(exp, ctxt).boolValue(ctxt);
	}

	public static Value evalBreakpointCondition(PExp exp, Context ctxt)
	{
		if (exp instanceof BreakpointExpression)
		{
			return ((BreakpointExpression) exp).eval(ctxt);
		}
		try
		{
			return exp.apply(VdmRuntime.getExpressionEvaluator(), ctxt);
		} catch (AnalysisException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
