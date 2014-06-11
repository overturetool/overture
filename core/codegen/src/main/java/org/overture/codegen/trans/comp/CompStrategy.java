package org.overture.codegen.trans.comp;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.AbstractIterationStrategy;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected SExpCG predicate;
	protected AIdentifierPatternCG idPattern;
	protected STypeCG compType;
	
	public CompStrategy(TransformationAssistantCG transformationAssistant, SExpCG predicate, String varName, STypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, langIterator, tempGen, varPrefixes);
		
		this.predicate = predicate;
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(varName);
		
		this.idPattern = idPattern;
		this.compType = compType;
	}
	
	protected abstract SExpCG getEmptyCollection();

	protected abstract List<SStmCG> getConditionalAdd(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id);
	
	protected List<SStmCG> consConditionalAdd(AIdentifierVarExpCG compResult,
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
		SExpCG emptyCollection = getEmptyCollection();
		emptyCollection.setType(compType.clone());
		
		AVarLocalDeclCG compResultInit = new AVarLocalDeclCG();
		compResultInit.setType(compType.clone());
		compResultInit.setPattern(idPattern.clone());
		compResultInit.setExp(emptyCollection);
		
		return packDecl(compResultInit);
	}
}
