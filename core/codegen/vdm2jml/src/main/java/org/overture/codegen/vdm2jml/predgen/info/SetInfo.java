package org.overture.codegen.vdm2jml.predgen.info;

import org.overture.codegen.runtime.V2J;

public class SetInfo extends AbstractSetSeqInfo
{
	public static final String IS_SET_METHOD = "isSet";
	
	public SetInfo(boolean optional, AbstractTypeInfo elementType)
	{
		super(optional, elementType);
	}

	@Override
	public String consCollectionCheck(String arg)
	{
		//e.g. (V2J.isSet(xs) && (\forall int i; 0 <= i && i < V2J.size(xs); Utils.is_nat(V2J.get(xs,i))));
		return consSubjectCheck(V2J.class.getSimpleName(), IS_SET_METHOD, arg);
	}

}
