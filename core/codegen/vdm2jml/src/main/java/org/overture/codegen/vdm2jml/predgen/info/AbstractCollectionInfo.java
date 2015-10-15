package org.overture.codegen.vdm2jml.predgen.info;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.runtime.V2J;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.util.NameGen;

public abstract class AbstractCollectionInfo extends AbstractTypeInfo
{
	public static final String GET_METHOD = "get";
	public static final String ITE_VAR_NAME_PREFIX = "i";
	public static final String SIZE__METHOD = "size";
	
	protected AbstractTypeInfo elementType;

	public AbstractCollectionInfo(boolean optional, AbstractTypeInfo elementType)
	{
		super(optional);
		this.elementType = elementType;
	}

	@Override
	public boolean allowsNull()
	{
		return optional;
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		return new LinkedList<>();
	}
	
	abstract public String consCollectionCheck(String arg);

	@Override
	public String consCheckExp(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen)
	{
		String isColCheck = consCollectionCheck(arg);
		String sizeCall = consSubjectCheck(V2J.class.getSimpleName(), SIZE__METHOD, arg);
		String iteVar = nameGen.getName(ITE_VAR_NAME_PREFIX);
		String elementArg = consSubjectCheckExtraArg(V2J.class.getSimpleName(), GET_METHOD, arg, iteVar);
		String elementCheck = elementType.consCheckExp(enclosingClass, javaRootPackage, elementArg, nameGen);
		
		StringBuilder sb = new StringBuilder();
		sb.append(isColCheck);
		sb.append(JmlGenerator.JML_AND);
		sb.append('(');
		sb.append(String.format("\\forall int %1$s; 0 <= %1$s && %1$s < ", iteVar));
		sb.append(sizeCall);
		sb.append("; ");
		sb.append(elementCheck);
		sb.append(')');
		
		// (V2J.isSeq(seq) && (\forall int i; 0 <= i && i < V2JL.size(seq); Utils.is_nat(V2JL.get(seq,i))));
		String seqCheckExp = "(" + sb.toString() + ")";
		
		if(allowsNull())
		{
			return "(" + consIsNullCheck(arg) + JmlGenerator.JML_OR + seqCheckExp + ")";
		}
		else
		{
			return seqCheckExp;
		}
	}
}