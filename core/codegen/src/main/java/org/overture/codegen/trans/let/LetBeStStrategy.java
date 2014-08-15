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
package org.overture.codegen.trans.let;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStNoBindingRuntimeErrorExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	private String successVarName;
	private SExpCG suchThat;
	private SSetTypeCG setType;

	public LetBeStStrategy(TransformationAssistantCG transformationAssistant,
			SExpCG suchThat, SSetTypeCG setType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, varPrefixes);

		String successVarNamePrefix = transformationAssistant.getVarPrefixes().getSuccessVarNamePrefix();
		ITempVarGen tempVarNameGen = transformationAssistant.getInfo().getTempVarNameGen();

		this.successVarName = tempVarNameGen.nextVarName(successVarNamePrefix);
		this.suchThat = suchThat;
		this.setType = setType;
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids)
			throws AnalysisException
	{
		List<AVarLocalDeclCG> outerBlockDecls = new LinkedList<AVarLocalDeclCG>();

		for (AIdentifierPatternCG id : ids)
		{
			outerBlockDecls.add(transformationAssistant.consIdDecl(setType, id.getName()));
		}

		outerBlockDecls.add(transformationAssistant.consBoolVarDecl(successVarName, false));

		return outerBlockDecls;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		SExpCG left = langIterator.getForLoopCond(setVar, ids, id);
		SExpCG right = transformationAssistant.consBoolCheck(successVarName, true);

		return transformationAssistant.consAndExp(left, right);
	}

	@Override
	public AVarLocalDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public AAssignmentStmCG getNextElementAssigned(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return langIterator.getNextElementAssigned(setVar, ids, id);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return packStm(transformationAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<SStmCG> getOuterBlockStms(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids)
	{
		ALetBeStNoBindingRuntimeErrorExpCG noBinding = new ALetBeStNoBindingRuntimeErrorExpCG();
		noBinding.setType(new AErrorTypeCG());

		ARaiseErrorStmCG raise = new ARaiseErrorStmCG();
		raise.setError(noBinding);

		AIfStmCG ifStm = new AIfStmCG();
		ifStm.setIfExp(transformationAssistant.consBoolCheck(successVarName, true));
		ifStm.setThenStm(raise);

		return packStm(ifStm);
	}
}
