package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;

/**
 * This class implements an extended version of the ProductBasisChecker visitor identifying if a node is of product
 * type.
 * 
 * @author kel
 */
public class ProductExtendedChecker extends
		QuestionAnswerAdaptor<Integer, Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public ProductExtendedChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseABracketType(ABracketType type, Integer size)
			throws AnalysisException
	{
		return type.getType().apply(THIS, size);
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type,
			Integer size) throws AnalysisException
	{

		if (type.getOpaque())
		{
			return false;
		}
		return type.getType().apply(THIS, size);
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type, Integer size)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType type, Integer size)
			throws AnalysisException
	{
		return type.getType().apply(THIS, size);
	}

	@Override
	public Boolean caseAParameterType(AParameterType type, Integer size)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAProductType(AProductType type, Integer size)
			throws AnalysisException
	{
		return size == 0 || type.getTypes().size() == size;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type, Integer size)
			throws AnalysisException
	{
		return AUnionTypeAssistantTC.getProduct(type, size) != null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type, Integer size)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultPType(PType type, Integer size)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

}
