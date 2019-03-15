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
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Checks if a type extending class element is of a specific PType.
 * 
 * @author gkanos
 */
public class PTypeExtendedChecker extends
		QuestionAnswerAdaptor<Class<? extends PType>, Boolean>
{
	protected final ITypeCheckerAssistantFactory af;
	protected final String fromModule;

	public PTypeExtendedChecker(ITypeCheckerAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public Boolean caseABracketType(ABracketType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return type.getType().apply(THIS, typeclass);
	}

	@Override
	public Boolean caseANamedInvariantType(ANamedInvariantType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		if (TypeChecker.isOpaque(type, fromModule)) return false;
		return type.getType().apply(THIS, typeclass);
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return typeclass.isInstance(type);
	}

	@Override
	public Boolean caseAOptionalType(AOptionalType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		if (typeclass.equals(AVoidType.class))
		{
			return false; // Optionals are never void
		}

		return type.getType().apply(THIS, typeclass);
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type,
			Class<? extends PType> typeclass) throws AnalysisException
	{
		for (PType t : type.getTypes())
		{
			if (af.createPTypeAssistant().isType(t, typeclass))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public Boolean defaultPType(PType type, Class<? extends PType> typeclass)
			throws AnalysisException
	{
		return typeclass.isInstance(type);
	}

	@Override
	public Boolean createNewReturnValue(INode node,
			Class<? extends PType> question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node,
			Class<? extends PType> question) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}
}
