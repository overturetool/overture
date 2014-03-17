package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class QuantifierStrategy extends AbstractIterationStrategy
{
	protected PExpCG predicate;
	protected String resultVarName;
	protected OrdinaryQuantifier quantifier;
	
	public QuantifierStrategy(TransformationAssistantCG transformationAssistant,
			PExpCG predicate, String resultVarName, OrdinaryQuantifier quantifier)
	{
		super(transformationAssistant);
		this.predicate = predicate;
		this.resultVarName = resultVarName;
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
		return transformationAssistant.consForCondition(iteratorName, resultVarName, quantifier == OrdinaryQuantifier.EXISTS);
	}

	@Override
	public ABlockStmCG getForLoopBody(PTypeCG setElementType,
			AIdentifierPatternCG id, String iteratorName)
			throws AnalysisException
	{
		return transformationAssistant.consForBodyNextElementDeclared(setElementType, id.getName(), iteratorName);
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return lastBind ? packStm(transformationAssistant.consBoolVarAssignment(predicate, resultVarName)) : null;
	}

	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return null;
	}
}
