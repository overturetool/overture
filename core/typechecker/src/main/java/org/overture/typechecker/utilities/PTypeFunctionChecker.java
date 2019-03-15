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
package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to check if a node is a function.
 * 
 * @author kel
 */
public class PTypeFunctionChecker extends AnswerAdaptor<Boolean>
{
	protected final ITypeCheckerAssistantFactory af;
	protected final String fromModule;

	public PTypeFunctionChecker(ITypeCheckerAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public Boolean caseABracketType(ABracketType node) throws AnalysisException
	{
		return node.getType().apply(THIS);
	}

	@Override
	public Boolean caseAFunctionType(AFunctionType node)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType node)
			throws AnalysisException
	{
		if (node instanceof ANamedInvariantType)
		{
			if (TypeChecker.isOpaque(node, fromModule)) return false;
			return ((ANamedInvariantType) node).getType().apply(THIS);
		}
		else
		{
			return false;
		}
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType node)
			throws AnalysisException
	{
		return node.getType().apply(THIS);
	}

	@Override
	public Boolean caseAUnionType(AUnionType node) throws AnalysisException
	{

		// return af.createAUnionTypeAssistant().getFunction(node) != null;
		return node.apply(af.getFunctionTypeFinder()) != null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType node) throws AnalysisException
	{

		return true;
	}

	@Override
	public Boolean defaultPType(PType node) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(INode node)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public Boolean createNewReturnValue(Object node)
	{
		assert false : "should not happen";
		return null;
	}
}
