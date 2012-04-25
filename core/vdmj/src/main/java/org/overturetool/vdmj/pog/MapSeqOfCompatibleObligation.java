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

import org.overturetool.vdmj.expressions.MapEnumExpression;
import org.overturetool.vdmj.expressions.MapletExpression;

public class MapSeqOfCompatibleObligation extends ProofObligation
{
	public MapSeqOfCompatibleObligation(MapEnumExpression exp, POContextStack ctxt)
	{
		super(exp.location, POType.MAP_SEQ_OF_COMPATIBLE, ctxt);
		StringBuilder sb = new StringBuilder();

		String m1 = getVar("m");
		String m2 = getVar("m");

		sb.append("forall " + m1 + ", " + m2 + " in set {");
		String prefix = "";

		for (MapletExpression m: exp.members)
		{
			sb.append(prefix);
			sb.append("{");
			sb.append(m);
			sb.append("}");
			prefix = ", ";
		}

		String d1 = getVar("d");
		String d2 = getVar("d");

		sb.append("} &\n  forall " + d1 + " in set dom " + m1 + ", " + d2 + " in set dom " + m2 + " &\n");
		sb.append("    " + d1 + " = " + d2 + " => " + m1 + "(" + d1 + ") = " + m2 + "(" + d2 + ")");

		value = ctxt.getObligation(sb.toString());
	}
}
