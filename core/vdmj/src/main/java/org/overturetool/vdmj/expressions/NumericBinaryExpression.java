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
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;

abstract public class NumericBinaryExpression extends BinaryExpression
{
	private static final long serialVersionUID = 1L;

	public NumericBinaryExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	abstract public Type typeCheck(
		Environment env, TypeList qualifiers, NameScope scope);

	protected void checkNumeric(Environment env, NameScope scope)
	{
		ltype = left.typeCheck(env, null, scope);
		rtype = right.typeCheck(env, null, scope);

		if (!ltype.isNumeric())
		{
			report(3139, "Left hand of " + op + " is not numeric");
			detail("Actual", ltype);
			ltype = new RealType(location);
		}

		if (!rtype.isNumeric())
		{
			report(3140, "Right hand of " + op + " is not numeric");
			detail("Actual", rtype);
			rtype = new RealType(location);
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		if (ltype.isUnion())
		{
			obligations.add(
				new SubTypeObligation(left, new RealType(left.location), ltype, ctxt));
		}

		if (rtype.isUnion())
		{
			obligations.add(
				new SubTypeObligation(right, new RealType(right.location), rtype, ctxt));
		}

		obligations.addAll(left.getProofObligations(ctxt));
		obligations.addAll(right.getProofObligations(ctxt));
		return obligations;
	}
}
