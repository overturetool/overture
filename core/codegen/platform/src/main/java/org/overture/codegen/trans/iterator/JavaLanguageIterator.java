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

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class JavaLanguageIterator extends AbstractLanguageIterator
{
	private static final String GET_ITERATOR = "iterator";
	private static final String NEXT_ELEMENT_ITERATOR = "next";
	private static final String HAS_NEXT_ELEMENT_ITERATOR = "hasNext";
	private static final String ITERATOR_TYPE = "Iterator";

	public JavaLanguageIterator(
			TransAssistantIR transformationAssistant,IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssistant, iteVarPrefixes);
	}

	protected String iteratorName;

	@Override
	public List<SStmIR> getPreForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		//Generate nothing
		return null;
	}
	
	@Override
	public AVarDeclIR getForLoopInit(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		iteratorName = transAssistant.getInfo().getTempVarNameGen().nextVarName(iteVarPrefixes.iterator());
		String setName = setVar.getName();
		AClassTypeIR iteratorType = transAssistant.consClassType(ITERATOR_TYPE);
		STypeIR setType = setVar.getType().clone();
		SExpIR getIteratorCall = transAssistant.consInstanceCall(setType, setName, iteratorType.clone(), GET_ITERATOR);

		return transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(iteratorType,
				transAssistant.getInfo().getPatternAssistant().consIdPattern(iteratorName),getIteratorCall);
	}

	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		AClassTypeIR iteratorType = transAssistant.consClassType(ITERATOR_TYPE);

		return transAssistant.consInstanceCall(iteratorType, iteratorName, new ABoolBasicTypeIR(), HAS_NEXT_ELEMENT_ITERATOR);
	}

	@Override
	public SExpIR getForLoopInc(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return null;
	}

	@Override
	public AVarDeclIR getNextElementDeclared(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		STypeIR elementType = transAssistant.getElementType(setVar.getType());

		return transAssistant.consNextElementDeclared(transAssistant.consClassType(ITERATOR_TYPE), elementType, pattern, iteratorName, NEXT_ELEMENT_ITERATOR);
	}

	@Override
	public ALocalPatternAssignmentStmIR getNextElementAssigned(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern, AVarDeclIR successVarDecl,
			AVarDeclIR nextElementDecl) throws AnalysisException
	{
		STypeIR elementType = transAssistant.getElementType(setVar.getType());

		return transAssistant.consNextElementAssignment(transAssistant.consClassType(ITERATOR_TYPE), elementType, pattern, iteratorName, NEXT_ELEMENT_ITERATOR, nextElementDecl);
	}
	
	@Override
	public SExpIR consNextElementCall(AIdentifierVarExpIR setVar) throws AnalysisException
	{
		STypeIR elementType = transAssistant.getSetTypeCloned(setVar).getSetOf();
		
		return transAssistant.consNextElementCall(transAssistant.consClassType(ITERATOR_TYPE), iteratorName, elementType, NEXT_ELEMENT_ITERATOR);
	}
}
