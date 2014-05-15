package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public class SeqCompStrategy extends CompStrategy
{
	protected PExpCG first;
	
	public SeqCompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, String var, PTypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);
		
		this.first = first;
	}
	
	@Override
	protected PExpCG getEmptyCollection()
	{
		return new AEnumSeqExpCG();
	}
	
	@Override
	protected List<PStmCG> getConditionalAdd(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		AIdentifierVarExpCG seqCompResult = new AIdentifierVarExpCG();
		seqCompResult.setType(compType.clone());
		seqCompResult.setOriginal(var);

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
	public List<PStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return getConditionalAdd(setVar, ids, id);
	}
}
