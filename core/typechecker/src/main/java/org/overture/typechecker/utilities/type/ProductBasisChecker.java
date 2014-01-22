package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type is a Product type
 * 
 * @author kel
 */
public class ProductBasisChecker extends TypeUnwrapper<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public ProductBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{

			if (type.getOpaque())
			{
				return false;
			}
			return ((ANamedInvariantType) type).getType().apply(THIS);
		} else
		{
			return false;
		}
	}

	@Override
	public Boolean caseAProductType(AProductType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return af.createAUnionTypeAssistant().getProduct(type) != null;
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
