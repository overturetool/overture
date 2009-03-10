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

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ExitException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.values.Value;

public class AlwaysStatement extends Statement
{
	private static final long serialVersionUID = 1L;

	public final Statement always;
	public final Statement body;

	public AlwaysStatement(LexLocation location, Statement always, Statement body)
	{
		super(location);
		this.always = always;
		this.body = body;
	}

	@Override
	public String toString()
	{
		return "always " + always + " in " + body;
	}

	@Override
	public String kind()
	{
		return "always";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		always.typeCheck(env, scope);
		return body.typeCheck(env, scope);
	}

	@Override
	public TypeSet exitCheck()
	{
		TypeSet types = new TypeSet();
		types.addAll(body.exitCheck());
		types.addAll(always.exitCheck());
		return types;
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;
		found = always.findStatement(lineno);
		if (found != null) return found;
		return body.findStatement(lineno);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value rv = null;
		ExitException bodyRaised = null;

		try
		{
			rv = body.eval(ctxt);
		}
		catch (ExitException e)
		{
			// Finally clause executes the "always" statement, but we
			// re-throw this exception, unless the always clause raises one.

			bodyRaised = e;
		}
		finally
		{
			always.eval(ctxt);

			if (bodyRaised != null)
			{
				throw bodyRaised;
			}
		}

		return rv;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = always.getProofObligations(ctxt);
		obligations.addAll(body.getProofObligations(ctxt));
		return obligations;
	}
}
