package org.overture.codegen.vdm2jml.predgen.info;

import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.runtime.V2J;
import org.overture.codegen.vdm2jml.util.NameGen;

public class MapInfo extends AbstractCollectionInfo
{
	public static final String IS_MAP_METHOD = "isMap";
	public static final String IS_INJECTIVE_MAP_METHOD = "isInjMap";
	public static final String GET_DOM_METHOD = "getDom";
	public static final String GET_RNG_METHOD = "getRng";
	
	private AbstractTypeInfo domType;
	private AbstractTypeInfo rngType;
	private boolean injective;
	
	public MapInfo(boolean optional, AbstractTypeInfo domType, AbstractTypeInfo rngType, boolean injective)
	{
		super(optional);
		this.domType = domType;
		this.rngType = rngType;
		this.injective = injective;
	}

	@Override
	public String consElementCheck(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen,
			String iteVar)
	{
		String domArg = consSubjectCheckExtraArg(V2J.class.getSimpleName(), GET_DOM_METHOD, arg, iteVar);
		String rngArg = consSubjectCheckExtraArg(V2J.class.getSimpleName(), GET_RNG_METHOD, arg, iteVar);
		
		String domCheck = domType.consCheckExp(enclosingClass, javaRootPackage, domArg, nameGen);
		String rngCheck = rngType.consCheckExp(enclosingClass, javaRootPackage, rngArg, nameGen);
		
		StringBuilder sb = new StringBuilder();
		sb.append(domCheck);
		sb.append(JmlGenerator.JML_AND);
		sb.append(rngCheck);
		
		return sb.toString();
	}

	@Override
	public String consCollectionCheck(String arg)
	{
		//e.g. (V2J.isMap(m) && (\forall int i; 0 <= i && i < VDM2JML.size(m); Utils.is_nat(VDM2JML.getDom(i,m)) && Utils.is_nat(VDM2JML.getRng(i,m))));
		return consSubjectCheck(V2J.class.getSimpleName(), injective ? IS_INJECTIVE_MAP_METHOD :IS_MAP_METHOD, arg);
	}
}
