package org.overture.codegen.transform;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.ACompSetExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class SetCompStrategy extends CompStrategy
{
	protected ACompSetExpCG setComp;
	
	public SetCompStrategy(TransformationAssistantCG transformationAssitant,
			ACompSetExpCG setComp)
	{
		super(transformationAssitant);
		this.setComp = setComp;
	}

	public PTypeCG getCollectionType() throws AnalysisException
	{
		return transformationAssitant.getSetTypeCloned(setComp.getSet());
	}

	@Override
	public String getVar()
	{
		return setComp.getVar();
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
	public PTypeCG getElementType() throws AnalysisException
	{
		return transformationAssitant.getSetTypeCloned(setComp.getSet()).getSetOf();
	}

	@Override
	public PExpCG getFirst()
	{
		return setComp.getFirst();
	}

	@Override
	public PExpCG getPredicate()
	{
		return setComp.getPredicate();
	}
}
