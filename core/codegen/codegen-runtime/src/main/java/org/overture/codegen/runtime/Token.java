/*
 * #%~
 * VDM Code Generator Runtime
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
package org.overture.codegen.runtime;

import java.io.Serializable;

public class Token implements Serializable
{
	private static final long serialVersionUID = -9188699884570692381L;

	private Object value;

	public Token(Object value)
	{
		this.value = value;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		if (value == null)
		{
			return 0;
		} else
		{
			return value.hashCode();
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Token))
		{
			return false;
		}

		Token other = (Token) obj;

		if (value == null && other.value != null
				|| value != null && !value.equals(other.value))
		{
			return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "mk_token(" + Utils.toString(value) + ")";
	}
}
