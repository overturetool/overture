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
package org.overture.ide.debug.core.model;

public class StringVdmType extends AtomicVdmType
{
	public StringVdmType(String name)
	{
		super(name);
	}

	public boolean isString()
	{
		return true;
	}

	public String formatValue(IVdmValue value)
	{
		String string = value.getRawValue();

		if (string == null)
		{
			return null;
		}
		return escapeString(string);
	}

	private static String escapeString(String string)
	{
		final boolean alreadyQuoted = isQuoted(string);
		final boolean escapeNeed = isEscapeNeeded(string, alreadyQuoted);
		if (!escapeNeed)
		{
			if (alreadyQuoted)
			{
				return string;
			} else
			{
				return '"' + string + '"';
			}
		}
		final StringBuffer escaped = new StringBuffer(string.length() + 8);
		if (!alreadyQuoted)
		{
			escaped.append('"');
		}
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			switch (c)
			{
				case '"':
					escaped.append("\\\""); //$NON-NLS-1$
					break;
				default:
					escaped.append(c);
					break;
			}
		}
		if (!alreadyQuoted)
		{
			escaped.append('"');
		}
		return escaped.toString();
	}

	private static boolean isQuoted(String string)
	{
		if (string.length() >= 2)
		{
			final char firstChar = string.charAt(0);
			final char lastChar = string.charAt(string.length() - 1);
			if (firstChar == '\'' && lastChar == '\'' || firstChar == '"'
					&& lastChar == '"')
			{
				return true;
			}
		}
		return false;
	}

	private static boolean isEscapeNeeded(String string, boolean isQuoted)
	{
		int i = 0, len = string.length();
		if (isQuoted)
		{
			++i;
			--len;
		}
		for (; i < len; ++i)
		{
			if (string.charAt(i) == '"')
			{
				return true;
			}
		}
		return false;
	}
}
