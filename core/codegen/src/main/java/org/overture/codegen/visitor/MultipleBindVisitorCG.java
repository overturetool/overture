/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.ir.IRInfo;

public class MultipleBindVisitorCG extends
		AbstractVisitorCG<IRInfo, SMultipleBindCG>
{

	@Override
	public SMultipleBindCG caseASetMultipleBind(ASetMultipleBind node,
			IRInfo question) throws AnalysisException
	{
		LinkedList<PPattern> patterns = node.getPlist();
		PExp set = node.getSet();

		LinkedList<SPatternCG> patternsCg = new LinkedList<SPatternCG>();

		for (PPattern pattern : patterns)
		{
			SPatternCG patternTempCg = pattern.apply(question.getPatternVisitor(), question);
			
			if (patternTempCg != null)
			{
				patternsCg.add(patternTempCg);
			}
			else
			{
				return null;
			}
		}

		SExpCG setCg = set.apply(question.getExpVisitor(), question);

		ASetMultipleBindCG multipleSetBind = new ASetMultipleBindCG();

		multipleSetBind.setPatterns(patternsCg);
		multipleSetBind.setSet(setCg);

		return multipleSetBind;
	}

}
