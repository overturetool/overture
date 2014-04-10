package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.expressions.AEnumMapExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AMapUnionBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public class MapCompStrategy extends ComplexCompStrategy
{
	protected AMapletExpCG first;
	
	public MapCompStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssitant,
			AMapletExpCG first, PExpCG predicate, String var, PTypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(config, transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);
		
		this.first = first;
	}
	
	@Override
	protected PExpCG getEmptyCollection()
	{
		return new AEnumMapExpCG();
	}

	@Override
	protected List<PStmCG> getConditionalAdd(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		AIdentifierVarExpCG mapCompResult = new AIdentifierVarExpCG();
		mapCompResult.setType(compType.clone());
		mapCompResult.setOriginal(var);

		AEnumMapExpCG mapToUnion = new AEnumMapExpCG();
		mapToUnion.setType(compType.clone());
		mapToUnion.getMembers().add(first.clone());
		
		AMapUnionBinaryExpCG mapUnion = new AMapUnionBinaryExpCG();
		mapUnion.setType(compType.clone());
		mapUnion.setLeft(mapCompResult.clone());
		mapUnion.setRight(mapToUnion);
		
		return consConditionalAdd(mapCompResult, mapUnion);
	}
}
