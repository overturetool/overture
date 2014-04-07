package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.AbstractLanguageIterator;
import org.overture.codegen.utils.TempVarNameGen;

public abstract class CompStrategy extends AbstractIterationStrategy
{
	protected PExpCG predicate;
	protected String var;
	protected PTypeCG compType;
	
	public abstract String getClassName();
	public abstract String getMemberName();
	public abstract PTypeCG getCollectionType() throws AnalysisException;
	
	public CompStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant, PExpCG predicate, String var, PTypeCG compType, AbstractLanguageIterator langIterator)
	{
		super(config, transformationAssistant, langIterator);
		
		this.predicate = predicate;
		this.var = var;
		this.compType = compType;
	}
	
	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(AIdentifierVarExpCG setVar, TempVarNameGen tempGen, TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		String className = getClassName();
		String memberName = getMemberName();
		PTypeCG collectionType = getCollectionType();
		
		return packDecl(transformationAssistant.consCompResultDecl(collectionType, var, className, memberName));
	}
}
