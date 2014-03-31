package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public abstract class QuantifierBaseStrategy extends
		AbstractIterationStrategy
{
	protected PExpCG predicate;
	protected String resultVarName;

	public QuantifierBaseStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssistant, PExpCG predicate, String resultVarName)
	{
		super(config, transformationAssistant);
		
		this.predicate = predicate;
		this.resultVarName = resultVarName;
	}
	
	@Override
	public ABlockStmCG getForLoopBody(PTypeCG setElementType,
			AIdentifierPatternCG id, String iteratorName)
			throws AnalysisException
	{
		return transformationAssistant.consForBodyNextElementDeclared(config.iteratorType(), setElementType, id.getName(), iteratorName, config.nextElement());
	}
	
	@Override
	public List<PStmCG> getOuterBlockStms()
	{
		return null;
	}
}