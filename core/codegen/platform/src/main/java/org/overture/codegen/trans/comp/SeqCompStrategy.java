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
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class SeqCompStrategy extends CompStrategy
{
	protected SExpCG first;

	public SeqCompStrategy(TransAssistantCG transformationAssitant,
			SExpCG first, SExpCG predicate, String var, STypeCG compType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			IterationVarPrefixes iteVarPrefixes)
	{
		super(transformationAssitant, predicate, var, compType, langIterator, tempGen, iteVarPrefixes);

		this.first = first;
	}

	@Override
	protected SExpCG getEmptyCollection()
	{
		return new AEnumSeqExpCG();
	}

	@Override
	protected List<SStmCG> getConditionalAdd(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		AIdentifierVarExpCG seqCompResult = new AIdentifierVarExpCG();
		seqCompResult.setType(compType.clone());
		seqCompResult.setName(idPattern.getName());
		seqCompResult.setIsLambda(false);
		seqCompResult.setIsLocal(true);

		AEnumSeqExpCG seqToConcat = new AEnumSeqExpCG();
		seqToConcat.setType(compType.clone());
		seqToConcat.getMembers().add(first.clone());

		ASeqConcatBinaryExpCG seqConcat = new ASeqConcatBinaryExpCG();
		seqConcat.setType(compType.clone());
		seqConcat.setLeft(seqCompResult.clone());
		seqConcat.setRight(seqToConcat);

		return consConditionalAdd(seqCompResult, seqConcat);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return getConditionalAdd(setVar, patterns, pattern);
	}
}
