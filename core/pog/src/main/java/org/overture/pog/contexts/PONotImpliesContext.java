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

import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.types.ABooleanBasicType;

public class PONotImpliesContext extends POContext
{
	public final PExp exp;

	public PONotImpliesContext(PExp exp)
	{
		this.exp = exp;
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		ANotUnaryExp notExp = new ANotUnaryExp();
		notExp.setExp(exp.clone());
		notExp.setType(new ABooleanBasicType());
		AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(notExp, stitch);
		return impliesExp;
	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("not ");
		sb.append(exp);
		sb.append(" =>");

		return sb.toString();
	}
}
