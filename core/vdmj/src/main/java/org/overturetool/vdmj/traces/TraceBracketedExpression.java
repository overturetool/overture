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

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;

/**
 * A class representing a core trace bracketed expression.
 */

public class TraceBracketedExpression extends TraceCoreDefinition
{
    private static final long serialVersionUID = 1L;
	public final List<TraceDefinitionTerm> terms;

	public TraceBracketedExpression(
		LexLocation location, List<TraceDefinitionTerm> terms)
	{
		super(location);
		this.terms = terms;
	}

	@Override
	public String toString()
	{
		return terms.toString();
	}

	@Override
	public void typeCheck(Environment env, NameScope scope)
	{
		for (TraceDefinitionTerm term: terms)
		{
			term.typeCheck(env, scope);
		}
	}

	@Override
	public TraceNode expand(Context ctxt)
	{
		SequenceTraceNode node = new SequenceTraceNode(ctxt);

		for (TraceDefinitionTerm term: terms)
		{
			node.nodes.add(term.expand(ctxt));
		}

		return node;
	}
}
