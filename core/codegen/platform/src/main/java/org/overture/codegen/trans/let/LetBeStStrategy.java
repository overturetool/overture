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
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStNoBindingRuntimeErrorExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	protected String successVarName;
	protected SExpCG suchThat;
	protected SSetTypeCG setType;

	protected int count = 0;
	protected List<AVarDeclCG> decls = new LinkedList<AVarDeclCG>();

	public LetBeStStrategy(TransAssistantCG transformationAssistant,
			SExpCG suchThat, SSetTypeCG setType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, iteVarPrefixes);

		String successVarNamePrefix = iteVarPrefixes.success();
		ITempVarGen tempVarNameGen = transformationAssistant.getInfo().getTempVarNameGen();

		this.successVarName = tempVarNameGen.nextVarName(successVarNamePrefix);
		this.suchThat = suchThat;
		this.setType = setType;
	}

	@Override
	public List<AVarDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
			throws AnalysisException
	{
		List<AVarDeclCG> outerBlockDecls = new LinkedList<AVarDeclCG>();

		for (SPatternCG id : patterns)
		{
			AVarDeclCG decl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(transAssistant.getSetTypeCloned(setType).getSetOf(),
					id.clone(), transAssistant.getInfo().getExpAssistant().consUndefinedExp());
			decls.add(decl);
			outerBlockDecls.add(decl);
		}

		successVarDecl = transAssistant.consBoolVarDecl(successVarName, false);
		outerBlockDecls.add(successVarDecl);

		return outerBlockDecls;
	}

	@Override
	public List<SStmCG> getPreForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		if (count > 0)
		{
			AAssignToExpStmCG successAssignment = new AAssignToExpStmCG();
			successAssignment.setExp(transAssistant.getInfo().getExpAssistant().consBoolLiteral(false));
			successAssignment.setTarget(transAssistant.consSuccessVar(successVarName));

			return packStm(successAssignment);
		} else
		{
			return null;
		}
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		SExpCG left = langIterator.getForLoopCond(setVar, patterns, pattern);
		SExpCG right = transAssistant.consBoolCheck(successVarName, true);

		return transAssistant.consAndExp(left, right);
	}

	@Override
	public DeclarationTag consDeclarationTag()
	{
		return new DeclarationTag(true, successVarDecl);
	}

	@Override
	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		AVarDeclCG nextElementDecl = decls.get(count++);
		tagNextElementDeclared(nextElementDecl);
		return null;
	}

	@Override
	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern) throws AnalysisException
	{
		return langIterator.getNextElementAssigned(setVar, patterns, pattern, successVarDecl, this.nextElementDeclared);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return packStm(transAssistant.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<SStmCG> getPostOuterBlockStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns)
	{
		ALetBeStNoBindingRuntimeErrorExpCG noBinding = new ALetBeStNoBindingRuntimeErrorExpCG();
		noBinding.setType(new AErrorTypeCG());

		ARaiseErrorStmCG raise = new ARaiseErrorStmCG();
		raise.setError(noBinding);

		AIfStmCG ifStm = new AIfStmCG();
		ifStm.setIfExp(transAssistant.consBoolCheck(successVarName, true));
		ifStm.setThenStm(raise);

		return packStm(ifStm);
	}
}
