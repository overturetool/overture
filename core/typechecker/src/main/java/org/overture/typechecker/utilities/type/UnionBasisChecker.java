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
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type is a Union type
 * 
 * @author kel
 */
public class UnionBasisChecker extends AnswerAdaptor<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public UnionBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseABracketType(ABracketType type) throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		if (type.getOpaque())
		{
			return false;
		}
		return type.getType().apply(THIS);
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType node)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType node)
			throws AnalysisException
	{
		return node.getType().apply(THIS);
	}
}
