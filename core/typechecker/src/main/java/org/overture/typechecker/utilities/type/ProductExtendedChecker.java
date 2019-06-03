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
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements an extended version of the ProductBasisChecker visitor identifying if a node is of product
 * type.
 * 
 * @author kel
 */
public class ProductExtendedChecker extends
		QuestionAnswerAdaptor<Integer, Boolean>
{
	protected final ITypeCheckerAssistantFactory af;
	protected final String fromModule;

	public ProductExtendedChecker(ITypeCheckerAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
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
		if (TypeChecker.isOpaque(type, fromModule)) return false;
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
		// return af.createAUnionTypeAssistant().getProduct(type, size) != null;
		return type.apply(af.getProductExtendedTypeFinder(fromModule), size) != null;
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
