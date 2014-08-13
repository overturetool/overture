package org.overture.codegen.trans.comp;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASetUnionBinaryExpCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class SetCompStrategy extends ComplexCompStrategy
{
	protected SExpCG first;
	
	public SetCompStrategy(TransformationAssistantCG transformationAssitant,
			SExpCG first, SExpCG predicate, String var, STypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
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
	protected List<SStmCG> getConditionalAdd(AIdentifierVarExpCG setVar, List<SPatternCG> patterns, SPatternCG pattern)
	{
		AIdentifierVarExpCG setCompResult = new AIdentifierVarExpCG();
		setCompResult.setType(compType.clone());
		setCompResult.setOriginal(idPattern.getName());

		AEnumSetExpCG setToUnion = new AEnumSetExpCG();
		setToUnion.setType(compType.clone());
		setToUnion.getMembers().add(first.clone());

		ASetUnionBinaryExpCG setUnion = new ASetUnionBinaryExpCG();
		setUnion.setType(compType.clone());
		setUnion.setLeft(setCompResult.clone());
		setUnion.setRight(setToUnion);
		
		return consConditionalAdd(setCompResult, setUnion);
	}
}
