/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.traces;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;

/**
 * A class representing a trace definition.
 */

public class TraceRepeatDefinition extends TraceDefinition
{
	public final TraceCoreDefinition core;
	public final long from;
	public final long to;

	public TraceRepeatDefinition(LexLocation location,
		TraceCoreDefinition core, long from, long to)
	{
		super(location);
		this.core = core;
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString()
	{
		return core +
			((from == 1 && to == 1) ? "" :
				(from == to) ? ("{" + from + "}") :
					("{" + from + ", " + to + "}"));
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		Environment local = base;
		core.typeCheck(local, scope);

		if (from > to)
		{
			TypeChecker.report(3277, "Trace repeat illegal values", location);
		}
	}

	@Override
	public TraceNode expand(Context ctxt)
	{
		TraceNode body = core.expand(ctxt);

		if (from == 1 && to == 1)
		{
			return body;
		}
		else
		{
			return new RepeatTraceNode(body, from, to);
		}
	}
}
