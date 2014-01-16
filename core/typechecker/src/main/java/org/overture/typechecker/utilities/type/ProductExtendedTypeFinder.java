package org.overture.typechecker.utilities.type;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * Used to check if a type of some size is a product type
 * 
 * @author kel
 */
public class ProductExtendedTypeFinder extends
		QuestionAnswerAdaptor<Integer, AProductType>
{

	protected ITypeCheckerAssistantFactory af;

	public ProductExtendedTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public AProductType caseABracketType(ABracketType type, Integer size)
			throws AnalysisException
	{
		return type.getType().apply(THIS, size);
	}

	@Override
	public AProductType caseANamedInvariantType(ANamedInvariantType type,
			Integer size) throws AnalysisException
	{
		return type.getType().apply(THIS, size);
	}

	@Override
	public AProductType defaultSInvariantType(SInvariantType type, Integer size)
			throws AnalysisException
	{
		assert false : "cannot getProduct from non-product type";
		return null;
	}

	@Override
	public AProductType caseAOptionalType(AOptionalType type, Integer size)
			throws AnalysisException
	{
		return type.getType().apply(THIS, size);
	}

	@Override
	public AProductType caseAProductType(AProductType type, Integer size)
			throws AnalysisException
	{
		return size == 0 || type.getTypes().size() == size ? type : null;
	}

	@Override
	public AProductType caseAUnionType(AUnionType type, Integer size)
			throws AnalysisException
	{
		if (type.getProdCard() != size)
		{
			type.setProdCard(size);
			type.setProdType(PTypeAssistantTC.getProduct(AstFactory.newAUnknownType(type.getLocation()), size));

			// Build a N-ary product type, making the types the union of the
			// original N-ary products' types...

			Map<Integer, PTypeSet> result = new HashMap<Integer, PTypeSet>();

			for (PType t : type.getTypes())
			{
				if (size == 0 && PTypeAssistantTC.isProduct(t)
						|| PTypeAssistantTC.isProduct(t, size))
				{
					AProductType pt = PTypeAssistantTC.getProduct(t, size);
					int i = 0;

					for (PType member : pt.getTypes())
					{
						PTypeSet ts = result.get(i);

						if (ts == null)
						{
							ts = new PTypeSet();
							result.put(i, ts);
						}

						ts.add(member);
						i++;
					}
				}
			}

			PTypeList list = new PTypeList();

			for (int i = 0; i < result.size(); i++)
			{
				list.add(result.get(i).getType(type.getLocation()));
			}

			type.setProdType(list.isEmpty() ? null
					: AstFactory.newAProductType(type.getLocation(), list));
		}

		return type.getProdType();
	}

	@Override
	public AProductType caseAUnknownType(AUnknownType type, Integer size)
			throws AnalysisException
	{
		NodeList<PType> tl = new NodeList<PType>(null);

		for (int i = 0; i < size; i++)
		{
			tl.add(AstFactory.newAUnknownType(type.getLocation()));
		}

		return AstFactory.newAProductType(type.getLocation(), tl);
	}

	@Override
	public AProductType createNewReturnValue(INode node, Integer question)
			throws AnalysisException
	{
		assert false : "cannot getProduct from non-product type";
		return null;
	}

	@Override
	public AProductType createNewReturnValue(Object node, Integer question)
			throws AnalysisException
	{
		assert false : "cannot getProduct from non-product type";
		return null;
	}
}
