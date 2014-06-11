package org.overture.codegen.trans.comp;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public abstract class ComplexCompStrategy extends CompStrategy
{
	public ComplexCompStrategy(
			TransformationAssistantCG transformationAssitant, SExpCG predicate,
			String var, STypeCG compType, ILanguageIterator langIterator,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		super(transformationAssitant, predicate, var, compType, langIterator, tempGen, varPrefixes);
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids)
			throws AnalysisException
	{
		return firstBind ? super.getOuterBlockDecls(setVar, ids) : null;
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return lastBind ? getConditionalAdd(setVar, ids, id) : null;
	}
}