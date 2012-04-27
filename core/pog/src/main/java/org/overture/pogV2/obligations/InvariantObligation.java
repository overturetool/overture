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

package org.overture.pog.obligations;

import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.SInvariantType;


public class InvariantObligation extends ProofObligation
{
	public InvariantObligation(PExp arg, SInvariantType inv, POContextStack ctxt)
	{
		super(arg.getLocation(), POType.INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(inv.getInvDef().getName().name);
		sb.append("(");
		sb.append(arg);
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}

	public InvariantObligation(AMapInverseUnaryExp exp, POContextStack ctxt)
	{
		super(exp.getLocation(), POType.INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("is_(");
		sb.append(exp.getExp());
		sb.append(", inmap ");
		sb.append(exp.getMapType().getFrom());
		sb.append(" to ");
		sb.append(exp.getMapType().getTo());
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}
}
