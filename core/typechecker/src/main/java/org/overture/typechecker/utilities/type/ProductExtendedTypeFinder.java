/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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

/**
 * Used to check if a type of some size is a product type
 * 
 * @author kel
 */
public class ProductExtendedTypeFinder extends
		QuestionAnswerAdaptor<Integer, AProductType>
{
	protected final ITypeCheckerAssistantFactory af;
	protected final String fromModule;

	public ProductExtendedTypeFinder(ITypeCheckerAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
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
			type.setProdType(af.createPTypeAssistant().getProduct(AstFactory.newAUnknownType(type.getLocation()), size, fromModule));

			// Build a N-ary product type, making the types the union of the
			// original N-ary products' types...

			Map<Integer, PTypeSet> result = new HashMap<Integer, PTypeSet>();

			for (PType t : type.getTypes())
			{
				if (size == 0 && af.createPTypeAssistant().isProduct(t, null)
						|| af.createPTypeAssistant().isProduct(t, size, fromModule))
				{
					AProductType pt = af.createPTypeAssistant().getProduct(t, size, fromModule);
					int i = 0;

					for (PType member : pt.getTypes())
					{
						PTypeSet ts = result.get(i);

						if (ts == null)
						{
							ts = new PTypeSet(af);
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
