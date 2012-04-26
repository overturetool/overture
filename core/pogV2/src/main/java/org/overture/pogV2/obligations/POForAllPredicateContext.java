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

package org.overture.pogV2.obligations;

import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.PExp;

public class POForAllPredicateContext extends POForAllContext
{
	public final PExp predicate;

	public POForAllPredicateContext(AMapCompMapExp exp)
	{
		super(exp);
		this.predicate = exp.getPredicate();
	}

	public POForAllPredicateContext(ASetCompSetExp exp)
	{
		super(exp);
		this.predicate = exp.getPredicate();
	}

	public POForAllPredicateContext(ASeqCompSeqExp exp)
	{
		super(exp);
		this.predicate = exp.getPredicate();
	}

	public POForAllPredicateContext(AExists1Exp exp)
	{
		super(exp);
		this.predicate = exp.getPredicate();
	}

	public POForAllPredicateContext(ALetBeStExp exp)
	{
		super(exp);
		this.predicate = exp.getSuchThat();
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
