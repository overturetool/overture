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
package org.overture.codegen.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public abstract class AbstractIterationStrategy implements IIterationStrategy
{
	protected boolean firstBind;
	protected boolean lastBind;

	protected TransAssistantIR transAssist;
	protected ILanguageIterator langIterator;
	protected ITempVarGen tempGen;
	protected IterationVarPrefixes iteVarPrefixes;

	protected AVarDeclIR successVarDecl = null;

	protected AVarDeclIR nextElementDeclared = null;

	public AbstractIterationStrategy(TransAssistantIR transformationAssistant,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes)
	{
		this.transAssist = transformationAssistant;
		this.langIterator = langIterator;
		this.tempGen = tempGen;
		this.iteVarPrefixes = iteVarPrefixes;
	}

	@Override
	public List<AVarDeclIR> getOuterBlockDecls(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns) throws AnalysisException
	{
		return null;
	}

	public List<SStmIR> getPreForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return langIterator.getPreForLoopStms(setVar, patterns, pattern);
	}

	@Override
	public AVarDeclIR getForLoopInit(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return langIterator.getForLoopInit(setVar, patterns, pattern);
	}

	@Override
	public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		return langIterator.getForLoopCond(setVar, patterns, pattern);
	}

	@Override
	public SExpIR getForLoopInc(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return langIterator.getForLoopInc(setVar, patterns, pattern);
	}

	public void tagNextElementDeclared(AVarDeclIR nextElementDecl)
	{
		nextElementDecl.setTag(consDeclarationTag());
		this.nextElementDeclared = nextElementDecl;
	}

	public DeclarationTag consDeclarationTag()
	{
		return new DeclarationTag(false, successVarDecl);
	}

	@Override
	public AVarDeclIR getNextElementDeclared(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException
	{
		tagNextElementDeclared(langIterator.getNextElementDeclared(setVar, patterns, pattern));

		return nextElementDeclared;
	}

	@Override
	public ALocalPatternAssignmentStmIR getNextElementAssigned(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern) throws AnalysisException
	{
		return null;
	}

	@Override
	public List<SStmIR> getForLoopStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
	{
		return null;
	}

	@Override
	public List<SStmIR> getPostOuterBlockStms(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns)
	{
		return null;
	}

	@Override
	public void setFirstBind(boolean firstBind)
	{
		this.firstBind = firstBind;
	}

	@Override
	public void setLastBind(boolean lastBind)
	{
		this.lastBind = lastBind;
	}

	protected List<SStmIR> packStm(SStmIR stm)
	{
		List<SStmIR> stms = new LinkedList<SStmIR>();

		stms.add(stm);

		return stms;
	}

	protected List<AVarDeclIR> packDecl(AVarDeclIR decl)
	{
		List<AVarDeclIR> decls = new LinkedList<AVarDeclIR>();

		decls.add(decl);

		return decls;
	}
}
