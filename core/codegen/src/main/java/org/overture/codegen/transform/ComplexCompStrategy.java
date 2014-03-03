package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public abstract class ComplexCompStrategy extends CompStrategy
{
	protected boolean firstBind;
	protected boolean lastBind;

	public ComplexCompStrategy(
			TransformationAssistantCG transformationAssitant, PExpCG predicate,
			String var, PTypeCG compType)
	{
		super(transformationAssitant, predicate, var, compType);

		this.firstBind = false;
		this.lastBind = false;
	}
	
	protected abstract List<PStmCG> getConditionalAdd();
	
	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls(
			List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		return firstBind ? super.getOuterBlockDecls(ids) : null;
	}
	
	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return lastBind ? getConditionalAdd() : null;
	}

	public void setLastBind(boolean lastBind)
	{
		this.lastBind = lastBind;
	}

	public void setFirstBind(boolean firstBind)
	{
		this.firstBind = firstBind;
	}
}