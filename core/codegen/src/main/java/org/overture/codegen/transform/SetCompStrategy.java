package org.overture.codegen.transform;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class SetCompStrategy extends CompStrategy
{
	public SetCompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, PExpCG set, String var)
	{
		super(transformationAssitant, first, predicate, set, var);
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
		
		return transformationAssitant.getSetTypeCloned(set);
	}
}
