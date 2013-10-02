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

public class SeqBasisChecker extends TypeUnwrapper<Boolean>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public SeqBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean defaultSSeqType(SSeqType type) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
			{
				return ANamedInvariantTypeAssistantTC.isSeq((ANamedInvariantType) type);
			}
		else
		{
			return false;
		}
	}
	
	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return AUnionTypeAssistantTC.isSeq(type);
	}
	@Override
	public Boolean caseAUnknownType(AUnknownType type) throws AnalysisException
	{
		return AUnknownTypeAssistantTC.isSeq(type);
	}
	@Override
	public Boolean defaultPType(PType type) throws AnalysisException
	{
		return false;
	}

}
