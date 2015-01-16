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
package org.overture.codegen.trans.quantifier;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class OrdinaryQuantifierStrategy extends QuantifierBaseStrategy
{
	protected OrdinaryQuantifier quantifier;

	public OrdinaryQuantifierStrategy(
			TransAssistantCG transformationAssistant,
			SExpCG predicate, String resultVarName,
			OrdinaryQuantifier quantifier, ILanguageIterator langIterator,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, predicate, resultVarName, langIterator, tempGen, varPrefixes);
		this.quantifier = quantifier;
	}

	@Override
	public List<AVarDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
			throws AnalysisException
	{
		return firstBind ? packDecl(transAssistant.consBoolVarDecl(resultVarName, quantifier == OrdinaryQuantifier.FORALL))
				: null;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		SExpCG left = langIterator.getForLoopCond(setVar, patterns, pattern);
		SExpCG right = transAssistant.consBoolCheck(resultVarName, quantifier == OrdinaryQuantifier.EXISTS);

		return transAssistant.consAndExp(left, right);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return lastBind ? packStm(transAssistant.consBoolVarAssignment(predicate, resultVarName))
				: null;
	}
}
