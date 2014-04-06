package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.constants.TempVarPrefixes;
import org.overture.codegen.utils.TempVarNameGen;

public abstract class QuantifierBaseStrategy extends
		AbstractIteratorStrategy
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
	public ABlockStmCG getForLoopBody(AIdentifierVarExpCG setVar, TempVarNameGen tempGen, TempVarPrefixes varPrefixes, List<AIdentifierPatternCG> ids, AIdentifierPatternCG id)
			throws AnalysisException
	{
		return transformationAssistant.consForBodyNextElementDeclared(config.iteratorType(), transformationAssistant.getSetTypeCloned(setVar).getSetOf(), id.getName(), iteratorName, config.nextElement());
	}
}