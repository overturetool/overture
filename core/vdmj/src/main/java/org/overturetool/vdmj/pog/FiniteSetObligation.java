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

import org.overturetool.vdmj.expressions.SetCompExpression;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.types.SetType;

public class FiniteSetObligation extends ProofObligation
{
	public FiniteSetObligation(
		SetCompExpression exp, SetType settype, POContextStack ctxt)
	{
		super(exp.location, POType.FINITE_SET, ctxt);
		StringBuilder sb = new StringBuilder();

		String finmap = getVar("finmap");
		String findex = getVar("findex");

		sb.append("exists " + finmap + ":map nat to (");
		sb.append(settype.setof);
		sb.append(") &\n");
		sb.append("  forall ");
		String prefix = "";

		for (MultipleBind mb: exp.bindings)
		{
			sb.append(prefix);
			sb.append(mb);
			prefix = ", ";
		}

		sb.append(" &\n    ");
		sb.append(exp.predicate);
		sb.append(" => ");
		sb.append("exists " + findex + " in set dom " + finmap +
			" & " + finmap + "(" + findex + ") = ");
		sb.append(exp.first);

		value = ctxt.getObligation(sb.toString());
	}
}
