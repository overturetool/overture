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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class BlockStatement extends SimpleBlockStatement
{
	private static final long serialVersionUID = 1L;

	public final DefinitionList assignmentDefs;

	public BlockStatement(LexLocation location, DefinitionList assignmentDefs)
	{
		super(location);
		this.assignmentDefs = assignmentDefs;
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		// Each dcl definition is in scope for later definitions...

		Environment local = env;

		for (Definition d: assignmentDefs)
		{
			local = new FlatCheckedEnvironment(d, local, scope);	// cumulative
			d.implicitDefinitions(local);
			d.typeCheck(local, scope);
		}

		// For type checking purposes, the definitions are treated as
		// local variables. At runtime (below) they have to be treated
		// more like (updatable) state.

		return super.typeCheck(local, scope);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(\n");

		for (Definition d: assignmentDefs)
		{
			sb.append(d);
			sb.append("\n");
		}

		sb.append("\n");
		sb.append(super.toString());
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Context evalContext = new Context(location, "block statement", ctxt);

		for (Definition d: assignmentDefs)
		{
			evalContext.putList(d.getNamedValues(evalContext));
		}

		for (Statement s: statements)
		{
			Value rv = s.eval(evalContext);

			if (!rv.isVoid())
			{
				return rv;
			}
		}

		return new VoidValue();
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = assignmentDefs.getProofObligations(ctxt);
		obligations.addAll(super.getProofObligations(ctxt));
		return obligations;
	}
}
