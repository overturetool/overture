/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.utils;

import org.overture.ast.intf.lex.ILexNameToken;

public class LexNameTokenWrapper
{
	private ILexNameToken name;

	public LexNameTokenWrapper(ILexNameToken name)
	{
		this.name = name;
	}

	public ILexNameToken getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name.getName();
	}

	@Override
	public int hashCode()
	{
		int hashCode = 0;

		if (name != null)
		{
			hashCode += name.hashCode();
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof LexNameTokenWrapper))
		{
			return false;
		}

		LexNameTokenWrapper other = (LexNameTokenWrapper) obj;

		return name.getName().equals(other.getName().getName());
	}
}
