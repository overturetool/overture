package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class ForAllExpStrategy extends AbstractIterationStrategy
{
	private PExpCG predicate;
	private String resultVarName;

	public ForAllExpStrategy(TransformationAssistantCG transformationAssistant,
			PExpCG predicate, String resultVarName)
	{
		super(transformationAssistant);
		this.predicate = predicate;
		this.resultVarName = resultVarName;
	}

	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls(
			List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		return firstBind ? packDecl(transformationAssistant.consBoolVarDecl(resultVarName, true)) : null;
	}

	@Override
	public PExpCG getForLoopCond(String iteratorName) throws AnalysisException
	{
		return transformationAssistant.conForCondition(iteratorName, resultVarName, false);
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
