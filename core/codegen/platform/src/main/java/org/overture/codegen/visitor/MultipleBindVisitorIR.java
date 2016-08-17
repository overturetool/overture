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
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ASeqMultipleBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.patterns.ASeqMultipleBindIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.patterns.ATypeMultipleBindIR;

public class MultipleBindVisitorIR
		extends AbstractVisitorIR<IRInfo, SMultipleBindIR>
{
	@Override
	public SMultipleBindIR caseASetMultipleBind(ASetMultipleBind node,
			IRInfo question) throws AnalysisException
	{
		List<PPattern> patterns = node.getPlist();
		PExp set = node.getSet();

		LinkedList<SPatternIR> patternsCg = new LinkedList<SPatternIR>();

		for (PPattern pattern : patterns)
		{
			SPatternIR patternTempCg = pattern.apply(question.getPatternVisitor(), question);

			if (patternTempCg != null)
			{
				patternsCg.add(patternTempCg);
			} else
			{
				return null;
			}
		}

		SExpIR setCg = set.apply(question.getExpVisitor(), question);

		ASetMultipleBindIR multipleSetBind = new ASetMultipleBindIR();

		multipleSetBind.setPatterns(patternsCg);
		multipleSetBind.setSet(setCg);

		return multipleSetBind;
	}

	@Override
	public SMultipleBindIR caseATypeMultipleBind(ATypeMultipleBind node,
			IRInfo question) throws AnalysisException
	{
		List<PPattern> patterns = node.getPlist();
		PType boundType = node.getType();

		List<SPatternIR> patternsCg = new LinkedList<SPatternIR>();

		for (PPattern pattern : patterns)
		{
			SPatternIR patternTempCg = pattern.apply(question.getPatternVisitor(), question);

			if (patternTempCg != null)
			{
				patternsCg.add(patternTempCg);
			} else
			{
				return null;
			}
		}

		STypeIR boundTypeCg = boundType.apply(question.getTypeVisitor(), question);

		ATypeMultipleBindIR multipleSetBind = new ATypeMultipleBindIR();

		multipleSetBind.setPatterns(patternsCg);
		multipleSetBind.setType(boundTypeCg);

		return multipleSetBind;
	}

	@Override
	public SMultipleBindIR caseASeqMultipleBind(ASeqMultipleBind node,
			IRInfo question) throws AnalysisException
	{

		List<PPattern> patterns = node.getPlist();
		PExp set = node.getSeq();

		LinkedList<SPatternIR> patternsCg = new LinkedList<SPatternIR>();

		for (PPattern pattern : patterns)
		{
			SPatternIR patternTempCg = pattern.apply(question.getPatternVisitor(), question);

			if (patternTempCg != null)
			{
				patternsCg.add(patternTempCg);
			} else
			{
				return null;
			}
		}

		SExpIR seqCg = set.apply(question.getExpVisitor(), question);

		ASeqMultipleBindIR multipleSeqBind = new ASeqMultipleBindIR();

		multipleSeqBind.setPatterns(patternsCg);
		multipleSeqBind.setSeq(seqCg);

		return multipleSeqBind;
	}
}
