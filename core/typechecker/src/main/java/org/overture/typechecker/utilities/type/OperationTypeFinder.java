package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnknownTypeAssistantTC;
/**
 * Used to get a operation type from a type
 * 
 * @author kel
 */
public class OperationTypeFinder extends TypeUnwrapper<AOperationType>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public OperationTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	
	@Override
	public AOperationType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			return ANamedInvariantTypeAssistantTC.getOperation((ANamedInvariantType) type);
		}
		else
		{
			return null;
		}
	}
	@Override
	public AOperationType caseAOperationType(AOperationType type)
			throws AnalysisException
	{
		return type;
	}
	
	@Override
	public AOperationType caseAUnionType(AUnionType type)
			throws AnalysisException
	{
		return AUnionTypeAssistantTC.getOperation((AUnionType) type);
	}

	@Override
	public AOperationType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AUnknownTypeAssistantTC.getOperation((AUnknownType) type);
	}
	
	@Override
	public AOperationType defaultPType(PType type) throws AnalysisException
	{
		assert false : "Can't getOperation of a non-operation";
		return null;
	}

}
