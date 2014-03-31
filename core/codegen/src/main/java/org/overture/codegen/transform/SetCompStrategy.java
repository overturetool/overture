package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class SetCompStrategy extends ComplexCompStrategy
{
	protected PExpCG first;
	
	public SetCompStrategy(ITransformationConfig config, TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, String var, PTypeCG compType)
	{
		super(config, transformationAssitant, predicate, var, compType);
		
		this.first = first;
	}

	@Override
	public String getClassName()
	{
		return config.setUtilFile();
	}

	@Override
	public String getMemberName()
	{
		return config.setUtilEmptySetCall();
	}

	@Override
	public PTypeCG getCollectionType() throws AnalysisException
	{
		return transformationAssistant.getSetTypeCloned(compType);
	}

	@Override
	protected List<PStmCG> getConditionalAdd()
	{
		return packStm(transformationAssistant.consConditionalAdd(config.addElementToSet(), var, predicate, first));
	}
}
