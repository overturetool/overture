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

package org.overture.pog.contexts;

import org.overture.ast.expressions.ADefExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.util.Utils;

public class PODefContext extends POContext
{
	public final ADefExp exp;

	public PODefContext(ADefExp exp)
	{
		this.exp = exp;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (!exp.getLocalDefs().isEmpty())
		{
			sb.append("def ");
			sb.append(Utils.listToString(exp.getLocalDefs(), "; "));
			sb.append(" in");
		}

		return sb.toString();
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		ADefExp defExp = exp.clone();
		defExp.setExpression(stitch);
		return defExp;
	}

}
