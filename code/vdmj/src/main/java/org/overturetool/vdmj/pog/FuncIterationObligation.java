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

package org.overturetool.vdmj.pog;

import org.overturetool.vdmj.expressions.StarStarExpression;

public class FuncIterationObligation extends ProofObligation
{
	public FuncIterationObligation(
		StarStarExpression exp, String prename, POContextStack ctxt)
	{
		super(exp.location, POType.FUNC_ITERATION, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(exp.right);
		sb.append(" > 1 => forall arg:");
		sb.append(exp.rtype.getNumeric());

		if (prename != null)
		{
    		sb.append(" & ");
    		sb.append(prename);
    		sb.append("(arg) => ");
    		sb.append(prename);
    		sb.append("(");
    		sb.append(exp.left);
    		sb.append("(arg))");
		}
		else
		{
    		sb.append(" & pre_(");
    		sb.append(exp.left);
    		sb.append(", arg) => pre_(");
    		sb.append(exp.left);
    		sb.append(", ");
    		sb.append(exp.left);
    		sb.append("(arg))");
		}

		value = ctxt.getObligation(sb.toString());
	}
}
