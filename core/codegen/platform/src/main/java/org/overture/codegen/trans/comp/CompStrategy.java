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
package org.overture.codegen.trans.comp;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected SExpCG predicate;
	protected AIdentifierPatternCG idPattern;
	protected STypeCG compType;

	public CompStrategy(TransAssistantCG transformationAssistant,
			SExpCG predicate, String varName, STypeCG compType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, iteVarPrefixes);

		this.predicate = predicate;

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(varName);

		this.idPattern = idPattern;
		this.compType = compType;
	}

	protected abstract SExpCG getEmptyCollection();

	protected abstract List<SStmCG> getConditionalAdd(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern);

	protected List<SStmCG> consConditionalAdd(AIdentifierVarExpCG compResult,
			SBinaryExpCG collectionMerge)
	{
		AIdentifierVarExpCG result = transAssistant.getInfo().getExpAssistant().consIdVar(compResult.getName(),
				compResult.getType().clone());

		AAssignToExpStmCG updateCompResult = new AAssignToExpStmCG();
		updateCompResult.setTarget(result);
		updateCompResult.setExp(collectionMerge);

		if (predicate != null)
		{
			AIfStmCG condCollectionUnion = new AIfStmCG();
			condCollectionUnion.setIfExp(predicate.clone());
			condCollectionUnion.setThenStm(updateCompResult);

			return packStm(condCollectionUnion);
		}

		return packStm(updateCompResult);
	}

	@Override
	public List<AVarDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
			throws AnalysisException
	{
		SExpCG emptyCollection = getEmptyCollection();
		emptyCollection.setType(compType.clone());
		AVarDeclCG compResultInit = transAssistant.getInfo().getDeclAssistant().
				consLocalVarDecl(compType.clone(), idPattern.clone(), emptyCollection);

		return packDecl(compResultInit);
	}
}
