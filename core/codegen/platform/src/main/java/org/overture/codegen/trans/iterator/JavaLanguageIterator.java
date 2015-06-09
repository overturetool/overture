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
package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class JavaLanguageIterator extends AbstractLanguageIterator
{
	private static final String GET_ITERATOR = "iterator";
	private static final String NEXT_ELEMENT_ITERATOR = "next";
	private static final String HAS_NEXT_ELEMENT_ITERATOR = "hasNext";
	private static final String ITERATOR_TYPE = "Iterator";

	public JavaLanguageIterator(
			TransAssistantCG transformationAssistant,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, tempGen, varPrefixes);
	}

	protected String iteratorName;

	@Override
	public List<SStmCG> getPreForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		//Generate nothing
		return null;
	}
	
	@Override
	public AVarDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		iteratorName = tempGen.nextVarName(varPrefixes.getIteratorNamePrefix());
		String setName = setVar.getName();
		AClassTypeCG iteratorType = transAssistant.consClassType(ITERATOR_TYPE);
		STypeCG setType = setVar.getType().clone();
		SExpCG getIteratorCall = transAssistant.consInstanceCall(setType, setName, iteratorType.clone(), GET_ITERATOR);

		return transAssistant.getInfo().getDeclAssistant().
				consLocalVarDecl(iteratorType, transAssistant.consIdPattern(iteratorName), getIteratorCall);
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AClassTypeCG iteratorType = transAssistant.consClassType(ITERATOR_TYPE);

		return transAssistant.consInstanceCall(iteratorType, iteratorName, new ABoolBasicTypeCG(), HAS_NEXT_ELEMENT_ITERATOR);
	}

	@Override
	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return null;
	}

	@Override
	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		STypeCG elementType = transAssistant.getSetTypeCloned(setVar).getSetOf();

		return transAssistant.consNextElementDeclared(ITERATOR_TYPE, elementType, pattern, iteratorName, NEXT_ELEMENT_ITERATOR);
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern, AVarDeclCG successVarDecl,
			AVarDeclCG nextElementDecl) throws AnalysisException
	{
		STypeCG elementType = transAssistant.getSetTypeCloned(setVar).getSetOf();

		return transAssistant.consNextElementAssignment(ITERATOR_TYPE, elementType, pattern, iteratorName, NEXT_ELEMENT_ITERATOR, nextElementDecl);
	}
	
	@Override
	public SExpCG consNextElementCall(AIdentifierVarExpCG setVar) throws AnalysisException
	{
		STypeCG elementType = transAssistant.getSetTypeCloned(setVar).getSetOf();
		
		return transAssistant.consNextElementCall(ITERATOR_TYPE, iteratorName, elementType, NEXT_ELEMENT_ITERATOR);
	}
}
