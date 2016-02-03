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
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.patterns.ABoolPatternCG;
import org.overture.codegen.cgast.patterns.ACharPatternCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.cgast.patterns.AIntPatternCG;
import org.overture.codegen.cgast.patterns.ANullPatternCG;
import org.overture.codegen.cgast.patterns.AQuotePatternCG;
import org.overture.codegen.cgast.patterns.ARealPatternCG;
import org.overture.codegen.cgast.patterns.ARecordPatternCG;
import org.overture.codegen.cgast.patterns.AStringPatternCG;
import org.overture.codegen.cgast.patterns.ATuplePatternCG;
import org.overture.codegen.ir.IRInfo;

public class PatternVisitorCG extends AbstractVisitorCG<IRInfo, SPatternCG>
{
	@Override
	public SPatternCG caseAIdentifierPattern(AIdentifierPattern node,
			IRInfo question) throws AnalysisException
	{
		String name = node.getName().getName();

		AIdentifierPatternCG idCg = new AIdentifierPatternCG();
		idCg.setName(name);

		return idCg;
	}

	@Override
	public SPatternCG caseAIgnorePattern(AIgnorePattern node, IRInfo question)
			throws AnalysisException
	{
		return new AIgnorePatternCG();
	}

	@Override
	public SPatternCG caseABooleanPattern(ABooleanPattern node, IRInfo question)
			throws AnalysisException
	{
		boolean value = node.getValue().getValue();

		ABoolPatternCG boolPatternCg = new ABoolPatternCG();
		boolPatternCg.setValue(value);

		return boolPatternCg;
	}

	@Override
	public SPatternCG caseACharacterPattern(ACharacterPattern node,
			IRInfo question) throws AnalysisException
	{
		char value = node.getValue().getValue();

		ACharPatternCG charPatternCg = new ACharPatternCG();
		charPatternCg.setValue(value);

		return charPatternCg;
	}

	@Override
	public SPatternCG caseAIntegerPattern(AIntegerPattern node, IRInfo question)
			throws AnalysisException
	{
		long value = node.getValue().getValue();

		AIntPatternCG intPatternCg = new AIntPatternCG();
		intPatternCg.setValue(value);

		return intPatternCg;
	}

	@Override
	public SPatternCG caseANilPattern(ANilPattern node, IRInfo question)
			throws AnalysisException
	{
		return new ANullPatternCG();
	}

	@Override
	public SPatternCG caseAQuotePattern(AQuotePattern node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();
		
		AQuotePatternCG quotePatternCg = new AQuotePatternCG();
		quotePatternCg.setValue(value);

		question.registerQuoteValue(value);
		return quotePatternCg;
	}

	@Override
	public SPatternCG caseARealPattern(ARealPattern node, IRInfo question)
			throws AnalysisException
	{
		double value = node.getValue().getValue();

		ARealPatternCG realPatternCg = new ARealPatternCG();
		realPatternCg.setValue(value);

		return realPatternCg;
	}

	@Override
	public SPatternCG caseAStringPattern(AStringPattern node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();

		AStringPatternCG stringPatternCg = new AStringPatternCG();
		stringPatternCg.setValue(value);

		return stringPatternCg;
	}

	@Override
	public SPatternCG caseATuplePattern(ATuplePattern node, IRInfo question)
			throws AnalysisException
	{
		ATuplePatternCG tuplePatternCg = new ATuplePatternCG();

		for (PPattern currentPattern : node.getPlist())
		{
			SPatternCG patternCg = currentPattern.apply(question.getPatternVisitor(), question);
			
			if(patternCg != null)
			{
				tuplePatternCg.getPatterns().add(patternCg);
			}
			else
			{
				return null;
			}
		}

		return tuplePatternCg;
	}

	@Override
	public SPatternCG caseARecordPattern(ARecordPattern node, IRInfo question)
			throws AnalysisException
	{
		String typeName = node.getTypename().getName();
		PType type = node.getType();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		ARecordPatternCG recordPatternCg = new ARecordPatternCG();
		recordPatternCg.setTypename(typeName);
		recordPatternCg.setType(typeCg);

		for (PPattern currentPattern : node.getPlist())
		{
			SPatternCG patternCg = currentPattern.apply(question.getPatternVisitor(), question);
			
			if(patternCg != null)
			{
				recordPatternCg.getPatterns().add(patternCg);
			}
			else
			{
				return null;
			}
		}

		return recordPatternCg;
	}
}
