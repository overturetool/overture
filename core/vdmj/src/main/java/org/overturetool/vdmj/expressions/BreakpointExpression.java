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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.runtime.Breakpoint;
import org.overturetool.vdmj.runtime.BreakpointCondition;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.Value;

public class BreakpointExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	private final Breakpoint bp;
	private final BreakpointCondition cond;
	private final long arg;

	public BreakpointExpression(
		Breakpoint breakpoint, BreakpointCondition cond, long arg)
	{
		super(breakpoint.location);
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
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		return new BooleanType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		boolean rv = false;

		switch (cond)
		{
			case EQ:
				rv = (bp.hits == arg);
				break;

			case GT:
				rv = (bp.hits > arg);
				break;

			case GE:
				rv = (bp.hits >= arg);
				break;

			case MOD:
				rv = ((bp.hits % arg) == 0);
				break;
		}

		return new BooleanValue(rv);
	}

	@Override
	public String kind()
	{
		return "breakpoint condition";
	}
}
