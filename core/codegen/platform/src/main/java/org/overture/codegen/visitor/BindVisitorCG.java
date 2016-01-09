
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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SBindCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.patterns.ASetBindCG;
import org.overture.codegen.cgast.patterns.ATypeBindCG;
import org.overture.codegen.ir.IRInfo;

public class BindVisitorCG extends AbstractVisitorCG<IRInfo, SBindCG>
{
	@Override
	public SBindCG caseASetBind(ASetBind node, IRInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		SPatternCG patternCg = pattern.apply(question.getPatternVisitor(), question);

		PExp set = node.getSet();
		SExpCG setCg = set.apply(question.getExpVisitor(), question);

		ASetBindCG setBind = new ASetBindCG();
		setBind.setPattern(patternCg);
		setBind.setSet(setCg);

		return setBind;
	}
	
	@Override
	public SBindCG caseATypeBind(ATypeBind node, IRInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		SPatternCG patternCg = pattern.apply(question.getPatternVisitor(), question);

		PType boundType = node.getType();
		STypeCG boundTypeCg = boundType.apply(question.getTypeVisitor(), question);

		ATypeBindCG setBind = new ATypeBindCG();
		setBind.setPattern(patternCg);
		setBind.setType(boundTypeCg);

		return setBind;
	}
}
