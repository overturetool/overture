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

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.MapCompExpression;
import org.overturetool.vdmj.util.Utils;

public class MapSetOfCompatibleObligation extends ProofObligation
{
	public MapSetOfCompatibleObligation(Expression exp, POContextStack ctxt)
	{
		super(exp.location, POType.MAP_SET_OF_COMPATIBLE, ctxt);
		StringBuilder sb = new StringBuilder();
		append(sb, exp.toString());
		value = ctxt.getObligation(sb.toString());
	}

	public MapSetOfCompatibleObligation(MapCompExpression exp, POContextStack ctxt)
	{
		super(exp.location, POType.MAP_SET_OF_COMPATIBLE, ctxt);
		StringBuilder sb = new StringBuilder();
		append(sb, mapCompAsSet(exp));
		value = ctxt.getObligation(sb.toString());
	}

	private void append(StringBuilder sb, String exp)
	{
		String m1 = getVar("m");
		String m2 = getVar("m");

		sb.append("forall " + m1 + ", " + m2 + " in set ");
		sb.append(exp);

		String d1 = getVar("d");
		String d2 = getVar("d");

		sb.append(" &\n  forall " + d1 + " in set dom " + m1 + ", " +
									d2 + " in set dom " + m2 + " &\n");
		sb.append("    " + d1 + " = " + d2 + " => " +
						m1 + "(" + d1 + ") = " + m2 + "(" + d2 + ")");
	}

	private String mapCompAsSet(MapCompExpression exp)
	{
		return "{{" + exp.first + "} | " + Utils.listToString(exp.bindings) +
			(exp.predicate == null ? "}" : " & " + exp.predicate + "}");
	}
}
