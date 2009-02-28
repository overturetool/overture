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

import java.util.List;


import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;

/**
 * A class representing a let-definition trace binding.
 */

public class TraceLetDefBinding extends TraceDefinition
{
	public final List<ValueDefinition> localDefs;
	public final TraceDefinition body;

	public TraceLetDefBinding(
		LexLocation location, List<ValueDefinition> localDefs, TraceDefinition body)
	{
		super(location);
		this.localDefs = localDefs;
		this.body = body;
	}

	@Override
	public String toString()
	{
		String result = "let ";

		for (Definition d: localDefs)
		{
			result = result + d.toString() + " ";
		}

		return result + "in " + body;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		Environment local = base;

		for (Definition d: localDefs)
		{
			d.typeCheck(local, scope);
			local = new FlatCheckedEnvironment(d, local);
		}

		body.typeCheck(local, scope);
		local.unusedCheck(base);
	}

	@Override
	public TraceNode expand(TraceNode onto, Context ctxt)
	{
		Context evalContext = new Context(location, "let binding", ctxt);

		for (Definition d: localDefs)
		{
			evalContext.put(d.getNamedValues(evalContext));
		}

		return body.expand(onto, evalContext);
	}
}
