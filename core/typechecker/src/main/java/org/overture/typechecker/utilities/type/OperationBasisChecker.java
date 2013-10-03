package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;


/**
 * Used to determine if a type is a operation type
 * 
 * @author kel
 */
public class OperationBasisChecker extends TypeUnwrapper<Boolean>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ITypeCheckerAssistantFactory af;

	public OperationBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			if(type.getOpaque()) return false;
			return ((ANamedInvariantType) type).getType().apply(THIS);//PTypeAssistantTC.isOperation(type.getType());
		}
		else
		{
			return false;
		}
		
	}
	
	@Override
	public Boolean caseAOperationType(AOperationType node)
			throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return af.createAUnionTypeAssistant().getOperation(type) != null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean defaultPType(PType type) throws AnalysisException
	{
		return false;
	}

}
