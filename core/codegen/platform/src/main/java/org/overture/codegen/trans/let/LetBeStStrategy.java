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

import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ALetBeStNoBindingRuntimeErrorExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.ir.statements.ARaiseErrorStmIR;
import org.overture.codegen.ir.types.AErrorTypeIR;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.DeclarationTag;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class LetBeStStrategy extends AbstractIterationStrategy
{
	protected String successVarName;
	protected SExpIR suchThat;
	protected STypeIR setSeqType;

	protected int count = 0;
	protected List<AVarDeclIR> decls = new LinkedList<AVarDeclIR>();

	public LetBeStStrategy(TransAssistantIR transformationAssistant,
			SExpIR suchThat, STypeIR setSeqType, ILanguageIterator langIterator,
			ITempVarGen tempGen, IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, iteVarPrefixes);

		String successVarNamePrefix = iteVarPrefixes.success();
		ITempVarGen tempVarNameGen = transformationAssistant.getInfo().getTempVarNameGen();

		this.successVarName = tempVarNameGen.nextVarName(successVarNamePrefix);
		this.suchThat = suchThat;
		this.setSeqType = setSeqType;
	}

	@Override
	public List<AVarDeclIR> getOuterBlockDecls(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns) throws AnalysisException
	{
		List<AVarDeclIR> outerBlockDecls = new LinkedList<AVarDeclIR>();

		STypeIR elementType = transAssist.getElementType(setSeqType);

		for (SPatternIR id : patterns)
		{
			AVarDeclIR decl = transAssist.getInfo().getDeclAssistant().consLocalVarDecl(elementType.clone(), id.clone(), transAssist.getInfo().getExpAssistant().consUndefinedExp());
			decls.add(decl);
			outerBlockDecls.add(decl);
		}

		successVarDecl = transAssist.consBoolVarDecl(successVarName, false);
		outerBlockDecls.add(successVarDecl);

		return outerBlockDecls;
	}

	@Override
	public List<SStmIR> getPreForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		if (count > 0)
		{
			AAssignToExpStmIR successAssignment = new AAssignToExpStmIR();
			successAssignment.setExp(transAssist.getInfo().getExpAssistant().consBoolLiteral(false));
			successAssignment.setTarget(transAssist.consSuccessVar(successVarName));

			return packStm(successAssignment);
		} else
		{
			return null;
		}
	}

	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		SExpIR left = langIterator.getForLoopCond(setVar, patterns, pattern);
		SExpIR right = transAssist.consBoolCheck(successVarName, true);

		return transAssist.consAndExp(left, right);
	}

	@Override
	public DeclarationTag consDeclarationTag()
	{
		return new DeclarationTag(true, successVarDecl);
	}

	@Override
	public AVarDeclIR getNextElementDeclared(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		AVarDeclIR nextElementDecl = decls.get(count++);
		tagNextElementDeclared(nextElementDecl);
		return null;
	}

	@Override
	public ALocalPatternAssignmentStmIR getNextElementAssigned(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern) throws AnalysisException
	{
		return langIterator.getNextElementAssigned(setVar, patterns, pattern, successVarDecl, this.nextElementDeclared);
	}

	@Override
	public List<SStmIR> getForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return packStm(transAssist.consBoolVarAssignment(suchThat, successVarName));
	}

	@Override
	public List<SStmIR> getPostOuterBlockStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns)
	{
		ALetBeStNoBindingRuntimeErrorExpIR noBinding = new ALetBeStNoBindingRuntimeErrorExpIR();
		noBinding.setType(new AErrorTypeIR());

		ARaiseErrorStmIR raise = new ARaiseErrorStmIR();
		raise.setError(noBinding);

		AIfStmIR ifStm = new AIfStmIR();
		ifStm.setIfExp(transAssist.consBoolCheck(successVarName, true));
		ifStm.setThenStm(raise);

		return packStm(ifStm);
	}
}
