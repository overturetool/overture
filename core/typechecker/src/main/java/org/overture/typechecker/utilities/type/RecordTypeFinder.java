package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;

public class RecordTypeFinder extends TypeUnwrapper<Boolean>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public RecordTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			return ANamedInvariantTypeAssistantTC.isRecord((ANamedInvariantType) type);
		} 
		else if (type instanceof ARecordInvariantType)
		{
			return ARecordInvariantTypeAssistantTC.isRecord((ARecordInvariantType) type);
		}
		else
		{
			return false;
		}
	}
	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return AUnionTypeAssistantTC.isRecord(type);
	}
	
	@Override
	public Boolean defaultPType(PType node) throws AnalysisException
	{
		 return false;
	}

}
