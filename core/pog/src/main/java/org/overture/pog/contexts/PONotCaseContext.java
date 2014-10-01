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

import java.util.List;

import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.ContextHelper;

public class PONotCaseContext extends POContext
{
	public final PPattern pattern;
	public final PType type;
	public final PExp exp;
	public final IPogAssistantFactory assistantFactory;

	public PONotCaseContext(PPattern pattern, PType type, PExp exp,
			IPogAssistantFactory assistantFactory)
	{
		this.pattern = pattern;
		this.type = type;
		this.exp = exp;
		this.assistantFactory = assistantFactory;
	}

	@Override
	public PExp getContextNode(PExp stitch)
	{
		AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(getCaseExp(), stitch);
		return impliesExp;
	}

	private PExp getCaseExp()
	{
		if (assistantFactory.createPPatternAssistant().isSimple(pattern))
		{
			ANotUnaryExp notExp = new ANotUnaryExp();
			AEqualsBinaryExp equalsExp = AstExpressionFactory.newAEqualsBinaryExp(assistantFactory.createPPatternAssistant().getMatchingExpression(pattern.clone()), exp.clone());
			notExp.setExp(equalsExp);
			return notExp;

		} else
		{

			ANotUnaryExp notExp = new ANotUnaryExp();
			AExistsExp existsExp = new AExistsExp();
			List<PMultipleBind> bindList = ContextHelper.bindListFromPattern(pattern.clone(), type.clone());

			existsExp.setBindList(bindList);
			PExp matching = assistantFactory.createPPatternAssistant().getMatchingExpression(pattern);
			AEqualsBinaryExp equalsExp = AstExpressionFactory.newAEqualsBinaryExp(matching, exp.clone());

			existsExp.setPredicate(equalsExp);
			notExp.setExp(existsExp);
			return notExp;
		}

	}

	@Override
	public String getContext()
	{
		StringBuilder sb = new StringBuilder();

		if (assistantFactory.createPPatternAssistant().isSimple(pattern))
		{
			sb.append("not ");
			sb.append(pattern);
			sb.append(" = ");
			sb.append(exp);
		} else
		{
			PExp matching = assistantFactory.createPPatternAssistant().getMatchingExpression(pattern);

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
