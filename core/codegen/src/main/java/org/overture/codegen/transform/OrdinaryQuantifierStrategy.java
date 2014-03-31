package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;

public class OrdinaryQuantifierStrategy extends QuantifierBaseStrategy
{
	protected OrdinaryQuantifier quantifier;
	
	public OrdinaryQuantifierStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant,
			PExpCG predicate, String resultVarName, OrdinaryQuantifier quantifier)
	{
		super(config, transformationAssistant, predicate, resultVarName);
		this.quantifier = quantifier;
	}

	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		return firstBind ? packDecl(transformationAssistant.consBoolVarDecl(resultVarName, quantifier == OrdinaryQuantifier.FORALL)) : null;
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName) throws AnalysisException
	{
		return transformationAssistant.consForCondition(config.iteratorType(), iteratorName, resultVarName, quantifier == OrdinaryQuantifier.EXISTS, config.hasNextElement());
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return lastBind ? packStm(transformationAssistant.consBoolVarAssignment(predicate, resultVarName)) : null;
	}
}
