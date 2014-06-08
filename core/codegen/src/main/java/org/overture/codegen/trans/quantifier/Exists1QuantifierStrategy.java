package org.overture.codegen.trans.quantifier;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ACounterLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.iterator.ILanguageIterator;

public class Exists1QuantifierStrategy extends QuantifierBaseStrategy
{
	public Exists1QuantifierStrategy(
			TransformationAssistantCG transformationAssistant,
			SExpCG predicate, String resultVarName,
			ILanguageIterator langIterator, ITempVarGen tempGen,
			TempVarPrefixes varPrefixes)
	{
		super(transformationAssistant, predicate, resultVarName, langIterator, tempGen, varPrefixes);
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			AIdentifierVarExpCG setVar, List<AIdentifierPatternCG> ids)
			throws AnalysisException
	{
		ACounterLocalDeclCG counter = new ACounterLocalDeclCG();
		counter.setName(resultVarName);
		counter.setInit(transformationAssistant.getInfo().getExpAssistant().consIntLiteral(0));

		return firstBind ? packDecl(counter) : null;
	}

	@Override
	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		SExpCG left = langIterator.getForLoopCond(setVar, ids, id);
		SExpCG right = transformationAssistant.consLessThanCheck(resultVarName, 2);

		return transformationAssistant.consAndExp(left, right);
	}

	@Override
	public List<SStmCG> getForLoopStms(AIdentifierVarExpCG setVar,
			List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return lastBind ? packStm(transformationAssistant.consConditionalIncrement(resultVarName, predicate))
				: null;
	}
}
