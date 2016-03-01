package org.overture.codegen.vdm2jml.predgen.info;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.runtime.V2J;
import org.overture.codegen.vdm2jml.util.NameGen;

public abstract class AbstractCollectionInfo extends AbstractTypeInfo
{
	public static final String ITE_VAR_NAME_PREFIX = "i";
	public static final String SIZE__METHOD = "size";
	
	public AbstractCollectionInfo(boolean optional)
	{
		super(optional);
	}

	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		return new LinkedList<>();
	}

	abstract public String consElementCheck(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen, String iteVar);
	
	abstract public String consCollectionCheck(String arg);
	
	@Override
	public String consCheckExp(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen)
	{
		String isColCheck = consCollectionCheck(arg);
		String sizeCall = consSubjectCheck(V2J.class.getSimpleName(), SIZE__METHOD, arg);
		String iteVar = nameGen.getName(ITE_VAR_NAME_PREFIX);
		String elementCheck = consElementCheck(enclosingClass, javaRootPackage, arg, nameGen, iteVar);
		
		StringBuilder sb = new StringBuilder();
		sb.append(isColCheck);
		sb.append(JmlGenerator.JML_AND);
		sb.append('(');
		sb.append(String.format("\\forall int %1$s; 0 <= %1$s && %1$s < ", iteVar));
		sb.append(sizeCall);
		sb.append("; ");
		sb.append(elementCheck);
		sb.append(')');
		
		String seqCheckExp = "(" + sb.toString() + ")";
		
		if(isOptional())
		{
			return "(" + consIsNullCheck(arg) + JmlGenerator.JML_OR + seqCheckExp + ")";
		}
		else
		{
			return seqCheckExp;
		}
	}
}