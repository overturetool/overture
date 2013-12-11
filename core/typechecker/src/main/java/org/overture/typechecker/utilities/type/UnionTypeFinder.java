package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get a Union type from a type
 * 
 * @author kel
 */
public class UnionTypeFinder extends TypeUnwrapper<AUnionType>
{

	protected ITypeCheckerAssistantFactory af;

	public UnionTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public AUnionType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			return ((ANamedInvariantType) type).getType().apply(THIS);
		} else
		{
			return null;
		}
	}

	@Override
	public AUnionType caseAUnionType(AUnionType type) throws AnalysisException
	{
		return type;
	}

	@Override
	public AUnionType defaultPType(PType type) throws AnalysisException
	{
		assert false : " cannot getUnion from non-union";
		return null;
	}
}
