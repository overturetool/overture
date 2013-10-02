package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;

/**
 * Used to get a seq type from a type
 * 
 * @author kel
 */

public class SeqTypeFinder extends TypeUnwrapper<SSeqType>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ITypeCheckerAssistantFactory af;

	public SeqTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public SSeqType defaultSSeqType(SSeqType type) throws AnalysisException
	{
		return type;
	}
	
	@Override
	public SSeqType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			return ANamedInvariantTypeAssistantTC.getSeq((ANamedInvariantType) type);
		}
		else
		{
			return null;
		}
	}
	@Override
	public SSeqType caseAUnionType(AUnionType type) throws AnalysisException
	{
		return AUnionTypeAssistantTC.getSeq(type);
	}
	
	@Override
	public SSeqType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AUnknownTypeAssistantTC.getSeq(type);
	}
	
	@Override
	public SSeqType defaultPType(PType type) throws AnalysisException
	{
		assert false : "cannot getSeq from non-seq";
		return null;
	}

}
