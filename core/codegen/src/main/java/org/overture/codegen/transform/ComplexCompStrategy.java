package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public abstract class ComplexCompStrategy extends CompStrategy
{
	public ComplexCompStrategy(ITransformationConfig config,
			TransformationAssistantCG transformationAssitant, PExpCG predicate,
			String var, PTypeCG compType)
	{
		super(config, transformationAssitant, predicate, var, compType);
	}
	
	protected abstract List<PStmCG> getConditionalAdd();
	
	@Override
	public List<? extends SLocalDeclCG> getOuterBlockDecls(
			List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		return firstBind ? super.getOuterBlockDecls(ids) : null;
	}
	
	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return lastBind ? getConditionalAdd() : null;
	}
}