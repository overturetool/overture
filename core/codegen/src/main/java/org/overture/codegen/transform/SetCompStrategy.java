package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class SetCompStrategy extends CompStrategy
{
	private boolean firstBind;
	private boolean lastBind;
	
	public SetCompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, String var, PTypeCG compType)
	{
		super(transformationAssitant, first, predicate, var, compType);
		
		this.firstBind = false;
		this.lastBind = false;
	}

	@Override
	public String getClassName()
	{
		return IJavaCodeGenConstants.SET_UTIL_FILE;
	}

	@Override
	public String getMemberName()
	{
		return IJavaCodeGenConstants.SET_UTIL_EMPTY_SET_CALL;
	}

	@Override
	public PTypeCG getCollectionType() throws AnalysisException
	{
		
		return transformationAssitant.getSetTypeCloned(compType);
	}
	
	@Override
	public List<ALocalVarDeclCG> getOuterBlockDecls(
			List<AIdentifierPatternCG> ids) throws AnalysisException
	{
		return firstBind ? super.getOuterBlockDecls(ids) : null;
	}
	
	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return lastBind ? super.getLastForLoopStms() : null;
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
