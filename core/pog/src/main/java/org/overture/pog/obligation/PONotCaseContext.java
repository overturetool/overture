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

import java.util.List;

import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.pog.utility.ContextHelper;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class PONotCaseContext extends POContext
{
	public final PPattern pattern;
	public final PType type;
	public final PExp exp;

	public PONotCaseContext(PPattern pattern, PType type, PExp exp)
	{
		this.pattern = pattern;
		this.type = type;
		this.exp = exp;
	}


	@Override
	public PExp getContextNode(PExp stitch)
	{
		AImpliesBooleanBinaryExp impliesExp = new AImpliesBooleanBinaryExp();
		impliesExp.setLeft(getCaseExp());
		impliesExp.setRight(stitch);
		return impliesExp;

	}

	private PExp getCaseExp()
	{
		if (PPatternAssistantTC.isSimple(pattern))
		{
			ANotUnaryExp notExp = new ANotUnaryExp();
			AEqualsBinaryExp equalsExp = new AEqualsBinaryExp();
			equalsExp.setLeft(PPatternAssistantTC.getMatchingExpression(pattern));
			equalsExp.setRight(exp);
			notExp.setExp(equalsExp);
			return notExp;

		} else
		{

			ANotUnaryExp notExp = new ANotUnaryExp();
			AExistsExp existsExp = new AExistsExp();
			List<PMultipleBind> bindList = ContextHelper.bindListFromPattern(pattern, type);
			existsExp.setBindList(bindList);
			PExp matching = PPatternAssistantTC.getMatchingExpression(pattern);
			AEqualsBinaryExp equalsExp = new AEqualsBinaryExp();
			equalsExp.setLeft(matching);
			equalsExp.setRight(exp);
			existsExp.setPredicate(equalsExp);
			notExp.setExp(existsExp);
			return notExp;
		}

	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (PPatternAssistantTC.isSimple(pattern))
		{
			sb.append("not ");
			sb.append(pattern);
			sb.append(" = ");
			sb.append(exp);
		} else
		{
			PExp matching = PPatternAssistantTC.getMatchingExpression(pattern);

			sb.append("not exists ");
			sb.append(matching);
			sb.append(":");
			sb.append(type);
			sb.append(" & ");
			sb.append(matching);
			sb.append(" = ");
			sb.append(exp);
		}

		sb.append(" =>");

		return sb.toString();
	}
}
