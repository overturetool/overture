package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.ITempVarGen;

public abstract class ComplexCompStrategy extends CompStrategy
{
	public ComplexCompStrategy(ITransformationConfig config,
			TransformationAssistantCG transformationAssitant, PExpCG predicate,
			String var, PTypeCG compType, ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(config, transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);
	}
	
	protected abstract List<PStmCG> getConditionalAdd();
	
	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		return firstBind ? super.getOuterBlockDecls(setVar, ids) : null;
	}
	
	@Override
	public List<PStmCG> getForLoopStms(AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return lastBind ? getConditionalAdd() : null;
	}
}