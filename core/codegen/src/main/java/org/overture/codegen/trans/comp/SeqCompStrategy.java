package org.overture.codegen.trans.comp;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class SeqCompStrategy extends CompStrategy
{
	protected SExpCG first;
	
	public SeqCompStrategy(TransformationAssistantCG transformationAssitant,
			SExpCG first, SExpCG predicate, String var, STypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);
		
		this.first = first;
	}
	
	@Override
	protected SExpCG getEmptyCollection()
	{
		return new AEnumSeqExpCG();
	}
	
	@Override
	protected List<SStmCG> getConditionalAdd(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		AIdentifierVarExpCG seqCompResult = new AIdentifierVarExpCG();
		seqCompResult.setType(compType.clone());
		seqCompResult.setOriginal(idPattern.getName());

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
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return getConditionalAdd(setVar, ids, id);
	}
}
