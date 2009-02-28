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
import org.overturetool.vdmj.pog.PONotImpliesContext;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.Value;

public class OrExpression extends BooleanBinaryExpression
{
	public OrExpression(Expression left, LexToken op, Expression right)
	{
		super(left, op, right);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			Value lv = left.eval(ctxt);

			if (lv.isUndefined())
			{
				return lv;
			}
			else
			{
				boolean lb = lv.boolValue(ctxt);

				if (lb)
				{
					return lv;	// Stop after LHS
				}

				Value rv = right.eval(ctxt);

				if (lb)
				{
					return new BooleanValue(true);
				}
				else
				{
					return rv;
				}
			}
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		if (ltype.isUnion())
		{
			obligations.add(
				new SubTypeObligation(left, new BooleanType(left.location), ltype, ctxt));
		}

		if (rtype.isUnion())
		{
			ctxt.push(new PONotImpliesContext(left));
			obligations.add(new SubTypeObligation(
				right, new BooleanType(right.location), rtype, ctxt));
			ctxt.pop();
		}

		obligations.addAll(left.getProofObligations(ctxt));

		ctxt.push(new PONotImpliesContext(left));
		obligations.addAll(right.getProofObligations(ctxt));
		ctxt.pop();

		return obligations;
	}

	@Override
	public String kind()
	{
		return "or";
	}
}
