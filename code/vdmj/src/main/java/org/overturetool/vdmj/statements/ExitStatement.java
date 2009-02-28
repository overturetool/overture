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

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ExitException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.UndefinedValue;
import org.overturetool.vdmj.values.Value;

public class ExitStatement extends Statement
{
	public final Expression expression;
	private Type exptype = null;

	public ExitStatement(LexLocation location)
	{
		super(location);
		this.expression = null;
	}

	public ExitStatement(LexLocation location, Expression expression)
	{
		super(location);
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		return "exit" + (expression == null ? ";" : " (" + expression + ")");
	}

	@Override
	public String kind()
	{
		return "exit";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		if (expression != null)
		{
			exptype = expression.typeCheck(env, null, scope);
		}

		// This is unknown because the statement doesn't actually return a
		// value - so if this is the only statement in a body, it is not a
		// type error (should return the same type as the definition return
		// type).

		return new UnknownType(location);
	}

	@Override
	public TypeSet exitCheck()
	{
		TypeSet types = new TypeSet();

		if (expression == null)
		{
			types.add(new VoidType(location));
		}
		else
		{
			types.add(exptype);
		}

		return types;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		Value v = null;

		if (expression != null)
		{
			v = expression.eval(ctxt);
		}
		else
		{
			v = new UndefinedValue();
		}

		throw new ExitException(v, location, ctxt);			// BANG!!
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		if (expression != null)
		{
			obligations.addAll(expression.getProofObligations(ctxt));
		}

		return obligations;
	}
}
