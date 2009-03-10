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
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidReturnType;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidReturnValue;

public class ReturnStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression expression;

	public ReturnStatement(LexLocation location)
	{
		super(location);
		this.expression = null;
	}

	public ReturnStatement(LexLocation location, Expression expression)
	{
		super(location);
		this.expression = expression;
	}

	@Override
	public String toString()
	{
		return "return" + (expression == null ? "" : " (" + expression + ")");
	}

	@Override
	public String kind()
	{
		return "return";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		if (expression == null)
		{
			return new VoidReturnType(location);
		}
		else
		{
			return expression.typeCheck(env, null, scope);
		}
	}

	@Override
	public TypeSet exitCheck()
	{
		if (expression != null)
		{
			// TODO We don't know what an expression will raise
			return new TypeSet(new UnknownType(location));
		}
		else
		{
			return super.exitCheck();
		}
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		if (expression == null)
		{
			return new VoidReturnValue();
		}
		else
		{
			return expression.eval(ctxt);
		}
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
