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

import org.overture.ast.expressions.ACompBinaryExp;

public class MapComposeObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3501039332724576068L;

	public MapComposeObligation(ACompBinaryExp exp, POContextStack ctxt)
	{
		super(exp.getLocation(), POType.MAP_COMPOSE, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("rng(");
		sb.append(exp.getRight());
		sb.append(") subset dom(");
		sb.append(exp.getLeft());
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}
}
