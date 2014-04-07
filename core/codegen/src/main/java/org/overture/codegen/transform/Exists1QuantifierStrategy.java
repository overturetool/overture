package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ACounterLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.transform.iterator.ILanguageIterator;
import org.overture.codegen.utils.TempVarNameGen;

public class Exists1QuantifierStrategy extends QuantifierBaseStrategy
{
	public Exists1QuantifierStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant,
			PExpCG predicate, String resultVarName, ILanguageIterator langIterator)
	{
		super(config, transformationAssistant, predicate, resultVarName, langIterator);
	}
	
	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(AIdentifierVarExpCG setVar, TempVarNameGen tempGen, TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		ACounterLocalDeclCG counter = new ACounterLocalDeclCG();
		counter.setName(resultVarName);
		counter.setInit(transformationAssistant.getInfo().getExpAssistant().consIntLiteral(0));
		
		return firstBind ? packDecl(counter) : null;
	}
	
	@Override
	public PExpCG getForLoopCond(AIdentifierVarExpCG setVar, TempVarNameGen tempGen, TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id) throws AnalysisException
	{
		PExpCG left = langIterator.getForLoopCond(setVar, tempGen, varPrefixes, ids, id);
		PExpCG right = transformationAssistant.consLessThanCheck(resultVarName, 2);
		
		return transformationAssistant.consAndExp(left, right);
	}
	
	@Override
	public List<PStmCG> getForLoopStms(AIdentifierVarExpCG setVar, TempVarNameGen tempGen, TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
	{
		return lastBind ? packStm(transformationAssistant.consConditionalIncrement(resultVarName, predicate)) : null;
	}
}
