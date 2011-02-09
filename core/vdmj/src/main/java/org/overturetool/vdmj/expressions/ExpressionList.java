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

import java.util.Vector;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.ValueList;


@SuppressWarnings("serial")
public class ExpressionList extends Vector<Expression>
{
	@Override
	public String toString()
	{
		return Utils.listToString(this);
	}

	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = new ProofObligationList();

		for (Expression e: this)
		{
			list.addAll(e.getProofObligations(ctxt));
		}

		return list;
	}

	public Expression findExpression(int lineno)
	{
		for (Expression exp: this)
		{
			Expression found = exp.findExpression(lineno);
			if (found != null) return found;
		}

		return null;
	}

	public ValueList getValues(Context ctxt)
	{
		ValueList list = new ValueList();

		for (Expression exp: this)
		{
			list.addAll(exp.getValues(ctxt));
		}

		return list;
	}

	public ExpressionList getSubExpressions()
	{
		ExpressionList list = new ExpressionList();

		for (Expression exp: this)
		{
			list.addAll(exp.getSubExpressions());
		}

		return list;
	}
}
