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
package org.overture.ide.debug.utils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class VdmWordFinder
{

	public static IRegion findWord(IDocument document, int offset)
	{

		int start = -2;
		int end = -1;

		try
		{
			int pos = offset;
			char c;

			while (pos >= 0)
			{
				c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
				{
					break;
				}
				--pos;
			}
			start = pos;

			pos = offset;
			int length = document.getLength();

			while (pos < length)
			{
				c = document.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
				{
					break;
				}
				++pos;
			}
			end = pos;

		} catch (BadLocationException x)
		{
		}

		if (start >= -1 && end > -1)
		{
			if (start == offset && end == offset)
			{
				return new Region(offset, 0);
			} else if (start == offset)
			{
				return new Region(start, end - start);
			} else
			{
				return new Region(start + 1, end - start - 1);
			}
		}

		return null;
	}
}
