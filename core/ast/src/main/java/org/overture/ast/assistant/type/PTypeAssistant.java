/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.assistant.type;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;

public class PTypeAssistant implements IAstAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public boolean isNumeric(PType type, String fromModule)
	{
		try
		{
			return type.apply(af.getNumericFinder(fromModule));
		}
		catch (AnalysisException e)
		{
			return false;
		}
	}

	public SNumericBasicType getNumeric(PType type, String fromModule)
	{
		try
		{
			return type.apply(af.getNumericBasisChecker(fromModule));
		}
		catch (AnalysisException e)
		{
			return null;
		}
	}

	public int hashCode(PType type)
	{
		return internalHashCode(type);
	}

	protected int internalHashCode(PType type)
	{
		try
		{
			return type.apply(af.getHashChecker());
		} catch (AnalysisException e)
		{
			return type.getClass().hashCode();
		}
	}

	public int hashCode(List<PType> list)
	{
		int hashCode = 1;
		for (PType e : list)
		{
			hashCode = 31
					* hashCode
					+ (e == null ? 0
							: af.createPTypeAssistant().internalHashCode(e));
		}
		return hashCode;
	}

	public String getName(PType type)
	{
		return type.getLocation().getModule();
	}
}
