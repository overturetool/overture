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

import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;

abstract public class BinaryExpression extends Expression
{
	public final Expression left;
	public final Expression right;
	public final LexToken op;

	public Type ltype = null;
	public Type rtype = null;

	public BinaryExpression(Expression left, LexToken op, Expression right)
	{
		super(op.location);
		this.left = left;
		this.right = right;
		this.op = op;
	}

	protected final Type binaryCheck(Environment env, NameScope scope, Type expected)
	{
		ltype = left.typeCheck(env, null, scope);
		rtype = right.typeCheck(env, null, scope);

		if (!ltype.isType(expected.getClass()))
		{
			report(3065, "Left hand of " + op + " is not " + expected);
		}

		if (!rtype.isType(expected.getClass()))
		{
			report(3066, "Right hand of " + op + " is not " + expected);
		}

		return expected;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		found = left.findExpression(lineno);
		if (found != null) return found;

		return right.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();
		obligations.addAll(left.getProofObligations(ctxt));
		obligations.addAll(right.getProofObligations(ctxt));
		return obligations;
	}

	@Override
	public String toString()
	{
		return "(" + left + " " + op + " " + right + ")";
	}
}
