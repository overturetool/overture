package org.overture.codegen.transform;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;

public class SeqCompStrategy extends CompStrategy
{
	private SSeqTypeCG seqType;
	
	public SeqCompStrategy(TransformationAssistantCG transformationAssitant,
			PExpCG first, PExpCG predicate, PExpCG set, String var, SSeqTypeCG seqType)
	{
		super(transformationAssitant, first, predicate, set, var);
		this.seqType = seqType;
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
		return seqType;
	}
}
