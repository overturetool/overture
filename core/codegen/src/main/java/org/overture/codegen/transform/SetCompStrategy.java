package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASetUnionBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public class SetCompStrategy extends ComplexCompStrategy
{
	protected PExpCG first;
	
	public SetCompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, String var, PTypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);
		
		this.first = first;
	}
	
	@Override
	protected PExpCG getEmptyCollection()
	{
		return new AEnumSetExpCG();
	}

	@Override
	protected List<PStmCG> getConditionalAdd(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		AIdentifierVarExpCG setCompResult = new AIdentifierVarExpCG();
		setCompResult.setType(compType.clone());
		setCompResult.setOriginal(var);

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
