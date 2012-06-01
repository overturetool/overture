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

import org.overture.ast.expressions.AStarStarBinaryExp;


public class MapIterationObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9122478081832322687L;

	public MapIterationObligation(AStarStarBinaryExp exp, POContextStack ctxt)
	{
		super(exp.getLocation(), POType.MAP_ITERATION, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append(exp.getRight());
		sb.append(" = 0 or ");
		sb.append(exp.getRight());
		sb.append(" = 1 or ");
		sb.append("rng(");
		sb.append(exp.getLeft());
		sb.append(") subset dom(");
		sb.append(exp.getLeft());
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}
}
