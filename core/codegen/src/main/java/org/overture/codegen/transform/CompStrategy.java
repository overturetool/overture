package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected PExpCG predicate;
	protected AIdentifierPatternCG idPattern;
	protected PTypeCG compType;
	
	public CompStrategy(TransformationAssistantCG transformationAssistant, PExpCG predicate, String varName, PTypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, varPrefixes);
		
		this.predicate = predicate;
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(varName);
		
		this.idPattern = idPattern;
		this.compType = compType;
	}
	
	protected abstract PExpCG getEmptyCollection();

	protected abstract List<PStmCG> getConditionalAdd(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);
	
	protected List<PStmCG> consConditionalAdd(AIdentifierVarExpCG compResult,
			SBinaryExpCG collectionMerge)
	{
		AIdentifierStateDesignatorCG result = new AIdentifierStateDesignatorCG();
		result.setType(compResult.getType().clone());
		result.setName(compResult.getOriginal());

		AAssignmentStmCG updateCompResult = new AAssignmentStmCG();
		updateCompResult.setTarget(result);
		updateCompResult.setExp(collectionMerge);
		
		if(predicate != null)
		{
			AIfStmCG condCollectionUnion = new AIfStmCG();
			condCollectionUnion.setIfExp(predicate.clone());
			condCollectionUnion.setThenStm(updateCompResult);
			
			return packStm(condCollectionUnion);
		}
		
		return packStm(updateCompResult);
	}
	
	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids)
			throws AnalysisException
	{
		PExpCG emptyCollection = getEmptyCollection();
		emptyCollection.setType(compType.clone());
		
		AVarLocalDeclCG compResultInit = new AVarLocalDeclCG();
		compResultInit.setType(compType.clone());
		compResultInit.setPattern(idPattern.clone());
		compResultInit.setExp(emptyCollection);
		
		return packDecl(compResultInit);
	}
}
