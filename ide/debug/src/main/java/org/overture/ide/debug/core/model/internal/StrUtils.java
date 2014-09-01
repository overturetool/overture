/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.model.internal;

public class StrUtils
{

	public static boolean equals(String s1, String s2)
	{
		return s1 == null ? s2 == null : s1.equals(s2);
	}

	public static boolean isEmpty(String str)
	{
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str)
	{
		return str != null && str.length() != 0;
	}

	public static boolean isBlank(String str)
	{
		if (str == null)
		{
			return true;
		}
		final int strLen = str.length();
		if (strLen == 0)
		{
			return true;
		}
		for (int i = 0; i < strLen; i++)
		{
			if (Character.isWhitespace(str.charAt(i)) == false)
			{
				return false;
			}
		}
		return true;
	}
}
