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
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.util.Utils;

public class FunctionApplyObligation extends ProofObligation
{
	public FunctionApplyObligation(Expression root, ExpressionList args, String prename, POContextStack ctxt)
	{
		super(root.location, POType.FUNC_APPLY, ctxt);
		StringBuilder sb = new StringBuilder();

		if (prename == null)
		{
			sb.append("pre_(");
			sb.append(root);
			sb.append(", ");
			sb.append(Utils.listToString(args));
			sb.append(")");
		}
		else
		{
			sb.append(prename);
			sb.append(Utils.listToString("(", args, ", ", ")"));
		}

		value = ctxt.getObligation(sb.toString());
	}
}
