package org.overture.codegen.vdm2jml.predgen.info;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.runtime.V2J;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.util.NameGen;

public class SeqInfo extends AbstractTypeInfo
{
	public static final String GET_METHOD = "get";
	public static final String ITE_VAR_NAME_PREFIX = "i";
	public static final String SIZE__METHOD = "size";
	public static final String IS_SEQ_METHOD = "isSeq";
	public static final String IS_SEQ1_METHOD = "isSeq1";
	
	private AbstractTypeInfo elementType;
	private boolean isSeq1;
	
	public SeqInfo(boolean optional, AbstractTypeInfo elementType, boolean isSeq1)
	{
		super(optional);
		this.elementType = elementType;
		this.isSeq1 = isSeq1;
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

	@Override
	public String consCheckExp(String enclosingClass, String javaRootPackage, String arg, NameGen nameGen)
	{
		String isSeqCheck = consSubjectCheck(V2J.class.getSimpleName(), isSeq1 ? IS_SEQ1_METHOD : IS_SEQ_METHOD, arg);
		String sizeCall = consSubjectCheck(V2J.class.getSimpleName(), SIZE__METHOD, arg);
		String iteVar = nameGen.getName(ITE_VAR_NAME_PREFIX);
		String elemtnArg = consSubjectCheckExtraArg(V2J.class.getSimpleName(), GET_METHOD, arg, iteVar);
		String elemtCheck = elementType.consCheckExp(enclosingClass, javaRootPackage, elemtnArg, nameGen);
		
		StringBuilder sb = new StringBuilder();
		sb.append(isSeqCheck);
		sb.append(JmlGenerator.JML_AND);
		sb.append('(');
		sb.append(String.format("\\forall int %1$s; 0 <= %1$s && %1$s < ", iteVar));
		sb.append(sizeCall);
		sb.append("; ");
		sb.append(elemtCheck);
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
