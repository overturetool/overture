package org.overture.codegen.vdm2jml.predgen.info;

import org.overture.codegen.vdm2jml.runtime.V2J;
import org.overture.codegen.vdm2jml.util.NameGen;

public abstract class AbstractSetSeqInfo extends AbstractCollectionInfo
{
	public static final String GET_METHOD = "get";

	protected AbstractTypeInfo elementType;

	public AbstractSetSeqInfo(boolean optional, AbstractTypeInfo elementType)
	{
		super(optional);
		this.elementType = elementType;
	}

	@Override
	public String consElementCheck(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen, String iteVar)
	{
		String elementArg = consSubjectCheckExtraArg(V2J.class.getSimpleName(), GET_METHOD, arg, iteVar);
		return elementType.consCheckExp(enclosingClass, javaRootPackage, elementArg, nameGen);
	}
}