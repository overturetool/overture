package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class SetCompStrategy extends ComplexCompStrategy
{
	protected PExpCG first;
	
	public SetCompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, String var, PTypeCG compType)
	{
		super(transformationAssitant, predicate, var, compType);
		
		this.first = first;
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
		return transformationAssistant.getSetTypeCloned(compType);
	}

	@Override
	protected List<PStmCG> getConditionalAdd()
	{
		return packStm(transformationAssistant.consConditionalAdd(IJavaCodeGenConstants.ADD_ELEMENT_TO_SET, var, predicate, first));
	}
}
