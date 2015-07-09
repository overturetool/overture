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
import java.util.Vector;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.utility.ContextHelper;
import org.overture.pog.utility.UniqueNameGenerator;

public class POCaseContext extends POContext
{
	public final PPattern pattern;
	public final PType type;
	public final PExp exp;
	public final IPogAssistantFactory assistantFactory;

	
	public POCaseContext(PPattern pattern, PType type, PExp exp,
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
		if (assistantFactory.createPPatternAssistant().isSimple(pattern))
		{
			PExp matching = patternToExp(pattern,assistantFactory,new UniqueNameGenerator(exp));
			PExp premise = AstExpressionFactory.newAEqualsBinaryExp(matching.clone(), exp.clone());
			AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(premise, stitch);
			impliesExp.setType(new ABooleanBasicType());
			return impliesExp;
		} else
		{
			AExistsExp existsExp = new AExistsExp();
			List<PMultipleBind> bindList = ContextHelper.bindListFromPattern(pattern.clone(), type.clone());
			existsExp.setBindList(bindList);
			PExp matching = patternToExp(pattern,assistantFactory,new UniqueNameGenerator(exp));

			PExp premise = AstExpressionFactory.newAEqualsBinaryExp(matching.clone(), exp.clone());

			ALetDefExp letDefExp = new ALetDefExp();

			AEqualsDefinition local = new AEqualsDefinition();
			local.setPattern(pattern.clone());
			// local.setName(def.getName().clone());
			local.setTest(exp.clone());
			List<PDefinition> lDefs = new Vector<PDefinition>();
			lDefs.add(local);
			letDefExp.setLocalDefs(lDefs);
			letDefExp.setExpression(stitch);

			AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(premise, letDefExp);
			impliesExp.setType(new ABooleanBasicType());
			existsExp.setPredicate(impliesExp);

			return existsExp;
		}

	}

	@Override
	public String getContext()
	{
		return "";
	}
}
