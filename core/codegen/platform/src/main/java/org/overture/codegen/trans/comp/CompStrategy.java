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

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected SExpIR predicate;
	protected AIdentifierPatternIR idPattern;
	protected STypeIR compType;

	public CompStrategy(TransAssistantIR transformationAssistant,
			SExpIR predicate, String varName, STypeIR compType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, iteVarPrefixes);

		this.predicate = predicate;

		AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
		idPattern.setName(varName);

		this.idPattern = idPattern;
		this.compType = compType;
	}

	protected abstract SExpIR getEmptyCollection();

	protected abstract List<SStmIR> getConditionalAdd(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern);

	protected List<SStmIR> consConditionalAdd(AIdentifierVarExpIR compResult, SStmIR collectionAdd)
	{
		if (predicate != null)
		{
			AIfStmIR condCollectionUnion = new AIfStmIR();
			condCollectionUnion.setIfExp(predicate.clone());
			condCollectionUnion.setThenStm(collectionAdd);

			return packStm(condCollectionUnion);
		}
		else
		{
			return packStm(collectionAdd);
		}
	}

	@Override
	public List<AVarDeclIR> getOuterBlockDecls(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns)
			throws AnalysisException
	{
		SExpIR emptyCollection = getEmptyCollection();
		emptyCollection.setType(compType.clone());
		AVarDeclIR compResultInit = transAssist.getInfo().getDeclAssistant().
				consLocalVarDecl(compType.clone(), idPattern.clone(), emptyCollection);

		return packDecl(compResultInit);
	}
}
