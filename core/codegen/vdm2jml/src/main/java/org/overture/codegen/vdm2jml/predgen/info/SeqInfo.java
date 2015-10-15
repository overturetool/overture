package org.overture.codegen.vdm2jml.predgen.info;

import org.overture.codegen.runtime.V2J;

public class SeqInfo extends AbstractCollectionInfo
{
	public static final String IS_SEQ_METHOD = "isSeq";
	public static final String IS_SEQ1_METHOD = "isSeq1";
	
	protected boolean isSeq1;
	
	public SeqInfo(boolean optional, AbstractTypeInfo elementType, boolean isSeq1)
	{
		super(optional, elementType);
		this.isSeq1 = isSeq1;
	}

	@Override
	public String consCollectionCheck(String arg)
	{
		return consSubjectCheck(V2J.class.getSimpleName(), isSeq1 ? IS_SEQ1_METHOD : IS_SEQ_METHOD, arg);
	}
}
