package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get a Product type from a type
 * 
 * @author kel
 */
public class ProductTypeFinder extends TypeUnwrapper<AProductType>
{

	protected ITypeCheckerAssistantFactory af;

	public ProductTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public AProductType defaultSInvariantType(SInvariantType type)
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
	public AProductType caseAProductType(AProductType type)
			throws AnalysisException
	{
		return type;
	}

	@Override
	public AProductType caseAOptionalType(AOptionalType node)
			throws AnalysisException
	{
		return node.getType().apply(THIS);
	}

	@Override
	public AProductType caseAUnionType(AUnionType type)
			throws AnalysisException
	{
		return af.createAUnionTypeAssistant().getProduct(type, 0);
	}

	@Override
	public AProductType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newAProductType(type.getLocation(), new NodeList<PType>(null));
	}

	@Override
	public AProductType defaultPType(PType type) throws AnalysisException
	{
		assert false : "cannot getProduct from non-product type";
		return null;
	}
}
