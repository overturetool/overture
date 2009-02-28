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
import org.overturetool.vdmj.expressions.MapInverseExpression;
import org.overturetool.vdmj.types.InvariantType;

public class InvariantObligation extends ProofObligation
{
	public InvariantObligation(Expression arg, InvariantType inv, POContextStack ctxt)
	{
		super(arg.location, POType.INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(inv.invdef.name.name);
		sb.append("(");
		sb.append(arg);
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}

	public InvariantObligation(MapInverseExpression exp, POContextStack ctxt)
	{
		super(exp.location, POType.INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("is_(");
		sb.append(exp.exp);
		sb.append(", inmap ");
		sb.append(exp.type.from);
		sb.append(" to ");
		sb.append(exp.type.to);
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}
}
