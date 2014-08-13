package org.overture.codegen.trans.quantifier;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class OrdinaryQuantifierStrategy extends QuantifierBaseStrategy
{
	protected OrdinaryQuantifier quantifier;

	public OrdinaryQuantifierStrategy(
			TransformationAssistantCG transformationAssistant,
			SExpCG predicate, String resultVarName,
			OrdinaryQuantifier quantifier, ILanguageIterator langIterator,
			ITempVarGen tempGen, TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, predicate, resultVarName, langIterator, tempGen, varPrefixes);
		this.quantifier = quantifier;
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns)
			throws AnalysisException
	{
		return firstBind ? packDecl(transformationAssistant.consBoolVarDecl(resultVarName, quantifier == OrdinaryQuantifier.FORALL))
				: null;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException
	{
		SExpCG left = langIterator.getForLoopCond(setVar, patterns, pattern);
		SExpCG right = transformationAssistant.consBoolCheck(resultVarName, quantifier == OrdinaryQuantifier.EXISTS);

		return transformationAssistant.consAndExp(left, right);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
	{
		return lastBind ? packStm(transformationAssistant.consBoolVarAssignment(predicate, resultVarName))
				: null;
	}
}
