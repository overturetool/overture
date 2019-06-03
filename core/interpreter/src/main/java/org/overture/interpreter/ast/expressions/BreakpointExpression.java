/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.ast.expressions;

import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.PExpBase;
import org.overture.ast.node.INode;
import org.overture.interpreter.runtime.Breakpoint;
import org.overture.interpreter.runtime.BreakpointCondition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.Value;

public class BreakpointExpression extends PExpBase
{
	private static final long serialVersionUID = 1L;
	private final Breakpoint bp;
	private final BreakpointCondition cond;
	private final long arg;

	@SuppressWarnings("deprecation")
	public BreakpointExpression(Breakpoint breakpoint,
			BreakpointCondition cond, long arg)
	{
		super(breakpoint.location, null);
		this.bp = breakpoint;
		this.cond = cond;
		this.arg = arg;
	}

	@Override
	public String toString()
	{
		return "hits " + cond + " " + arg;
	}

	@Override
	public PExp clone(Map<INode, INode> oldToNewMap)
	{
		return null;
	}

	@Override
	public PExp clone()
	{
		return null;
	}

	@Override
	public void apply(IAnalysis analysis) throws AnalysisException
	{

	}

	@Override
	public <A> A apply(IAnswer<A> caller) throws AnalysisException
	{
		return null;
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
			throws AnalysisException
	{

	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
			throws AnalysisException
	{
		return null;
	}

	// @Override
	// public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	// {
	// return new BooleanType(location);
	// }

	// @Override
	public Value eval(Context ctxt)
	{
		boolean rv = false;

		switch (cond)
		{
			case EQ:
				rv = bp.hits == arg;
				break;

			case GT:
				rv = bp.hits > arg;
				break;

			case GE:
				rv = bp.hits >= arg;
				break;

			case MOD:
				rv = bp.hits % arg == 0;
				break;
		}

		return new BooleanValue(rv);
	}
	//
	// @Override
	// public String kind()
	// {
	// return "breakpoint condition";
	// }
}
