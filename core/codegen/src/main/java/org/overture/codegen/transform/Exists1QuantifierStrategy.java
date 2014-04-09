package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ACounterLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;

public class Exists1QuantifierStrategy extends QuantifierBaseStrategy
{
	public Exists1QuantifierStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant,
			PExpCG predicate, String resultVarName)
	{
		super(config, transformationAssistant, predicate, resultVarName);
	}
	
	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		ACounterLocalDeclCG counter = new ACounterLocalDeclCG();
		counter.setName(resultVarName);
		counter.setInit(transformationAssistant.getInfo().getExpAssistant().consIntLiteral(0));
		
		return firstBind ? packDecl(counter) : null;
	}
	
	@Override
	public PExpCG getForLoopCond(String iteratorName) throws AnalysisException
	{
		return transformationAssistant.consForCondition(config.iteratorType(), iteratorName, resultVarName, transformationAssistant.consLessThanCheck(resultVarName, 2), config.hasNextElement());
	}
	
	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return lastBind ? packStm(transformationAssistant.consConditionalIncrement(resultVarName, predicate)) : null;
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return null;
	}
}
