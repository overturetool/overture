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
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.SSetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to determine if a type is a set type
 * 
 * @author kel
 */
public class SetBasisChecker extends TypeUnwrapper<String, Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public SetBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean defaultSSetType(SSetType type, String fromModule) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type, String fromModule)
			throws AnalysisException
	{
		if (TypeChecker.isOpaque(type, fromModule)) return false;

		if (type instanceof ANamedInvariantType)
		{
			return ((ANamedInvariantType) type).getType().apply(THIS, fromModule);
		}
		else
		{
			return false;
		}
	}

	@Override
	public Boolean caseAUnionType(AUnionType type, String fromModule) throws AnalysisException
	{
		return type.apply(af.getSetTypeFinder(), fromModule) != null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type, String fromModule) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultPType(PType type, String fromModule) throws AnalysisException
	{
		return false;
	}
}
