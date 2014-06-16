package org.overture.codegen.trans.comp;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.AEnumMapExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AMapUnionBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class MapCompStrategy extends ComplexCompStrategy
{
	protected AMapletExpCG first;

	public MapCompStrategy(TransformationAssistantCG transformationAssitant,
			AMapletExpCG first, SExpCG predicate, String var, STypeCG compType,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);

		this.first = first;
	}

	@Override
	protected SExpCG getEmptyCollection()
	{
		return new AEnumMapExpCG();
	}

	@Override
	protected List<SStmCG> getConditionalAdd(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		AIdentifierVarExpCG mapCompResult = new AIdentifierVarExpCG();
		mapCompResult.setType(compType.clone());
		mapCompResult.setOriginal(idPattern.getName());

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
