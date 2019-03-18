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
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to find type of the general PType
 * 
 * @author kel
 */
public class PTypeFinder extends QuestionAnswerAdaptor<String, PType>
{
	protected final ITypeCheckerAssistantFactory af;
	protected final String fromModule;

	public PTypeFinder(ITypeCheckerAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public PType caseABracketType(ABracketType type, String typename)
			throws AnalysisException
	{
		return type.getType().apply(THIS, typename);
	}

	@Override
	public PType caseANamedInvariantType(ANamedInvariantType type,
			String typename) throws AnalysisException
	{
		if (TypeChecker.isOpaque(type, fromModule)) return null;
		return type.getType().apply(THIS, typename);
	}

	@Override
	public PType caseARecordInvariantType(ARecordInvariantType type,
			String typename) throws AnalysisException
	{
		if (TypeChecker.isOpaque(type, fromModule)) return null;

		if (typename.indexOf('`') > 0)
		{
			return type.getName().getFullName().equals(typename) ? type : null;
		} else
		{
			// Local typenames aren't qualified with the local module name
			return type.getName().getName().equals(typename) ? type : null;
		}
	}

	@Override
	public PType defaultSInvariantType(SInvariantType type, String typename)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public PType caseAOptionalType(AOptionalType type, String typename)
			throws AnalysisException
	{
		return type.getType().apply(THIS, typename);
	}

	@Override
	public PType caseAUnionType(AUnionType type, String typename)
			throws AnalysisException
	{
		for (PType t : type.getTypes())
		{
			PType rt = af.createPTypeAssistant().isType(t, typename);

			if (rt != null)
			{
				return rt;
			}
		}

		return null;
	}

	@Override
	public PType caseAUnknownType(AUnknownType type, String typename)
			throws AnalysisException
	{
		return null;// Isn't any particular type? comment from original source
	}

	@Override
	public PType caseAUnresolvedType(AUnresolvedType type, String typename)
			throws AnalysisException
	{
		return type.getName().getFullName().equals(typename) ? type : null;
	}

	@Override
	public PType defaultPType(PType type, String typename)
			throws AnalysisException
	{
		return af.createPTypeAssistant().toDisplay(type).equals(typename) ? type
				: null;

	}

	@Override
	public PType createNewReturnValue(INode node, String question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, String question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
