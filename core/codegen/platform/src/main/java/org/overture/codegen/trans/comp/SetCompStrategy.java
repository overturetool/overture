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
import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASetUnionBinaryExpCG;
import org.overture.codegen.cgast.statements.ASetCompAddStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class SetCompStrategy extends ComplexCompStrategy
{
	protected SExpCG first;

	public SetCompStrategy(TransAssistantCG transformationAssitant,
			SExpCG first, SExpCG predicate, String var, STypeCG compType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);

		this.first = first;
	}

	@Override
	protected SExpCG getEmptyCollection()
	{
		return new AEnumSetExpCG();
	}

	@Override
	protected List<SStmCG> getConditionalAdd(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		AIdentifierVarExpCG setCompResult = new AIdentifierVarExpCG();
		setCompResult.setType(compType.clone());
		setCompResult.setName(idPattern.getName());
		setCompResult.setIsLambda(false);
		setCompResult.setIsLocal(true);

		ASetCompAddStmCG add = new ASetCompAddStmCG();
		add.setSet(setCompResult);
		add.setElement(first.clone());
		
		return consConditionalAdd(setCompResult, add);
	}
}
