package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class MapCompStrategy extends ComplexCompStrategy
{
	protected AMapletExpCG first;
	
	public MapCompStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssitant,
			AMapletExpCG first, PExpCG predicate, String var, PTypeCG compType)
	{
		super(config, transformationAssitant, predicate, var, compType);
		
		this.first = first;
	}

	@Override
	public String getClassName()
	{
		return config.mapUtilFile();
	}

	@Override
	public String getMemberName()
	{
		return config.mapUtilEmptyMapCall();
	}

	@Override
	public PTypeCG getCollectionType() throws AnalysisException
	{
		return transformationAssistant.getMapTypeCloned(compType);
	}

	@Override
	protected List<PStmCG> getConditionalAdd()
	{
		return packStm(transformationAssistant.consConditionalAdd(config.addElementToMap(), var, predicate, first.getLeft(), first.getRight()));
	}

}
