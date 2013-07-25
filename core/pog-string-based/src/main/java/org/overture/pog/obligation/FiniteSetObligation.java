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

package org.overture.pog.obligation;

import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.ASetType;

public class FiniteSetObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4471304924561635823L;

	public FiniteSetObligation(
		ASetCompSetExp exp, ASetType settype, POContextStack ctxt)
	{
		super(exp.getLocation(), POType.FINITE_SET, ctxt);
		StringBuilder sb = new StringBuilder();

		String finmap = getVar("finmap");
		String findex = getVar("findex");

		sb.append("exists " + finmap + ":map nat to (");
		sb.append(settype.getSetof());
		sb.append(") &\n");
		sb.append("  forall ");
		String prefix = "";

		for (PMultipleBind mb: exp.getBindings())
		{
			sb.append(prefix);
			sb.append(mb);
			prefix = ", ";
		}

		sb.append(" &\n    ");

		if (exp.getPredicate() != null)
		{
			sb.append(exp.getPredicate());
			sb.append(" => ");
		}

		sb.append("exists " + findex + " in set dom " + finmap +
			" & " + finmap + "(" + findex + ") = ");
		sb.append(exp.getFirst());

		value = ctxt.getObligation(sb.toString());
	}
}
