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
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type is a Union type
 * 
 * @author kel
 */
public class UnionBasisChecker extends TypeUnwrapper<String, Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public UnionBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseABracketType(ABracketType type, String fromModule) throws AnalysisException
	{
		return type.getType().apply(THIS, fromModule);
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type, String fromModule)
			throws AnalysisException
	{
		if (TypeChecker.isOpaque(type, fromModule)) return false;
		return type.getType().apply(THIS, fromModule);
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType node, String fromModule)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type, String fromModule) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean createNewReturnValue(INode node, String fromModule)
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node, String fromModule)
	{
		return false;
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType node, String fromModule)
			throws AnalysisException
	{
		return node.getType().apply(THIS, fromModule);
	}
}
