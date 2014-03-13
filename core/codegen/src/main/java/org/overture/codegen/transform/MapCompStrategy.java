package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class MapCompStrategy extends ComplexCompStrategy
{
	protected AMapletExpCG first;
	
	public MapCompStrategy(TransformationAssistantCG transformationAssitant,
			AMapletExpCG first, PExpCG predicate, String var, PTypeCG compType)
	{
		super(transformationAssitant, predicate, var, compType);
		
		this.first = first;
	}

	@Override
	public String getClassName()
	{
		return IJavaCodeGenConstants.MAP_UTIL_FILE;
	}

	@Override
	public String getMemberName()
	{
		return IJavaCodeGenConstants.MAP_UTIL_EMPTY_MAP_CALL;
	}

	@Override
	public PTypeCG getCollectionType() throws AnalysisException
	{
		return transformationAssistant.getMapTypeCloned(compType);
	}

	@Override
	protected List<PStmCG> getConditionalAdd()
	{
		return packStm(transformationAssistant.consConditionalAdd(IJavaCodeGenConstants.ADD_ELEMENT_TO_MAP, var, predicate, first.getLeft(), first.getRight()));
	}

}
