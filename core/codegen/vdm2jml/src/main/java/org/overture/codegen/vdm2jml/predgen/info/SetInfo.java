package org.overture.codegen.vdm2jml.predgen.info;

import org.overture.codegen.runtime.V2J;

public class SetInfo extends AbstractCollectionInfo
{
	public static final String IS_SET_METHOD = "isSet";
	
	public SetInfo(boolean optional, AbstractTypeInfo elementType)
	{
		super(optional, elementType);
	}

	@Override
	public String consCollectionCheck(String arg)
	{
		return consSubjectCheck(V2J.class.getSimpleName(), IS_SET_METHOD, arg);
	}

}
