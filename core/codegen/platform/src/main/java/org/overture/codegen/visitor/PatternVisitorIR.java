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
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.patterns.ABoolPatternIR;
import org.overture.codegen.ir.patterns.ACharPatternIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.patterns.AIgnorePatternIR;
import org.overture.codegen.ir.patterns.AIntPatternIR;
import org.overture.codegen.ir.patterns.ANullPatternIR;
import org.overture.codegen.ir.patterns.AQuotePatternIR;
import org.overture.codegen.ir.patterns.ARealPatternIR;
import org.overture.codegen.ir.patterns.ARecordPatternIR;
import org.overture.codegen.ir.patterns.AStringPatternIR;
import org.overture.codegen.ir.patterns.ATuplePatternIR;
import org.overture.codegen.ir.IRInfo;

public class PatternVisitorIR extends AbstractVisitorIR<IRInfo, SPatternIR>
{
	@Override
	public SPatternIR caseAIdentifierPattern(AIdentifierPattern node,
			IRInfo question) throws AnalysisException
	{
		String name = node.getName().getName();

		AIdentifierPatternIR idCg = new AIdentifierPatternIR();
		idCg.setName(name);

		return idCg;
	}

	@Override
	public SPatternIR caseAIgnorePattern(AIgnorePattern node, IRInfo question)
			throws AnalysisException
	{
		return new AIgnorePatternIR();
	}

	@Override
	public SPatternIR caseABooleanPattern(ABooleanPattern node, IRInfo question)
			throws AnalysisException
	{
		boolean value = node.getValue().getValue();

		ABoolPatternIR boolPatternCg = new ABoolPatternIR();
		boolPatternCg.setValue(value);

		return boolPatternCg;
	}

	@Override
	public SPatternIR caseACharacterPattern(ACharacterPattern node,
			IRInfo question) throws AnalysisException
	{
		char value = node.getValue().getValue();

		ACharPatternIR charPatternCg = new ACharPatternIR();
		charPatternCg.setValue(value);

		return charPatternCg;
	}

	@Override
	public SPatternIR caseAIntegerPattern(AIntegerPattern node, IRInfo question)
			throws AnalysisException
	{
		long value = node.getValue().getValue();

		AIntPatternIR intPatternCg = new AIntPatternIR();
		intPatternCg.setValue(value);

		return intPatternCg;
	}

	@Override
	public SPatternIR caseANilPattern(ANilPattern node, IRInfo question)
			throws AnalysisException
	{
		return new ANullPatternIR();
	}

	@Override
	public SPatternIR caseAQuotePattern(AQuotePattern node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();
		
		AQuotePatternIR quotePatternCg = new AQuotePatternIR();
		quotePatternCg.setValue(value);

		question.registerQuoteValue(value);
		return quotePatternCg;
	}

	@Override
	public SPatternIR caseARealPattern(ARealPattern node, IRInfo question)
			throws AnalysisException
	{
		double value = node.getValue().getValue();

		ARealPatternIR realPatternCg = new ARealPatternIR();
		realPatternCg.setValue(value);

		return realPatternCg;
	}

	@Override
	public SPatternIR caseAStringPattern(AStringPattern node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();

		AStringPatternIR stringPatternCg = new AStringPatternIR();
		stringPatternCg.setValue(value);

		return stringPatternCg;
	}

	@Override
	public SPatternIR caseATuplePattern(ATuplePattern node, IRInfo question)
			throws AnalysisException
	{
		ATuplePatternIR tuplePatternCg = new ATuplePatternIR();

		for (PPattern currentPattern : node.getPlist())
		{
			SPatternIR patternCg = currentPattern.apply(question.getPatternVisitor(), question);
			
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
	public SPatternIR caseARecordPattern(ARecordPattern node, IRInfo question)
			throws AnalysisException
	{
		String typeName = node.getTypename().getName();
		PType type = node.getType();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		ARecordPatternIR recordPatternCg = new ARecordPatternIR();
		recordPatternCg.setTypename(typeName);
		recordPatternCg.setType(typeCg);

		for (PPattern currentPattern : node.getPlist())
		{
			SPatternIR patternCg = currentPattern.apply(question.getPatternVisitor(), question);
			
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
