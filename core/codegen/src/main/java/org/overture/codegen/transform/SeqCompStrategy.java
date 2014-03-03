package org.overture.codegen.transform;

import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class SeqCompStrategy extends CompStrategy
{
	protected PExpCG first;
	
	public SeqCompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, String var, PTypeCG compType)
	{
		super(transformationAssitant, predicate, var, compType);
		
		this.first = first;
	}

	@Override
	public String getClassName()
	{
		return IJavaCodeGenConstants.SEQ_UTIL_FILE;
	}

	@Override
	public String getMemberName()
	{
		return IJavaCodeGenConstants.SEQ_UTIL_EMPTY_SEQ_CALL;
	}

	@Override
	public PTypeCG getCollectionType() throws AnalysisException
	{
		return transformationAssitant.getSeqTypeCloned(compType);
	}

	@Override
	public List<PStmCG> getLastForLoopStms()
	{
		return packStm(transformationAssitant.consConditionalAdd(IJavaCodeGenConstants.ADD_ELEMENT_TO_LIST, var, predicate, first));
	}
}
