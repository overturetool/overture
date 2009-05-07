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

import org.overturetool.vdmj.expressions.Exists1Expression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.LetBeStExpression;
import org.overturetool.vdmj.expressions.MapCompExpression;
import org.overturetool.vdmj.expressions.SeqCompExpression;
import org.overturetool.vdmj.expressions.SetCompExpression;

public class POForAllPredicateContext extends POForAllContext
{
	public final Expression predicate;

	public POForAllPredicateContext(MapCompExpression exp)
	{
		super(exp);
		this.predicate = exp.predicate;
	}

	public POForAllPredicateContext(SetCompExpression exp)
	{
		super(exp);
		this.predicate = exp.predicate;
	}

	public POForAllPredicateContext(SeqCompExpression exp)
	{
		super(exp);
		this.predicate = exp.predicate;
	}

	public POForAllPredicateContext(Exists1Expression exp)
	{
		super(exp);
		this.predicate = exp.predicate;
	}

	public POForAllPredicateContext(LetBeStExpression exp)
	{
		super(exp);
		this.predicate = exp.suchThat;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.getContext());

		if (predicate != null)
		{
			sb.append(" ");
			sb.append(predicate);
			sb.append(" =>");
		}

		return sb.toString();
	}
}
