/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.editor.syntax;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class TupleSelectRule implements IRule
{

	public static final int EOF = -1;

	IToken fToken = null;

	public TupleSelectRule(IToken token)
	{
		fToken = token;
	}

	public IToken evaluate(ICharacterScanner scanner)
	{
		int c = scanner.read();
		if (c == EOF)
		{
			scanner.unread();
			return Token.EOF;
		}

		if (c == '.')
		{
			c = scanner.read();
			if (c == EOF)
			{
				scanner.unread();
				return Token.EOF;
			} else if (c != '#')
			{
				scanner.unread();
				return Token.UNDEFINED;
			}
			int digitCount = 0;
			while (Character.isDigit((c = scanner.read())))
			{
				digitCount++;
			}
			
			boolean followedBySpace = Character.isWhitespace((char)c);
			
			
			scanner.unread(); //last one read in while
			if(digitCount==0)
			{
				scanner.unread();// #
			}else if(followedBySpace)
			{
				return fToken;
			}
		}

		scanner.unread();// .
		return Token.UNDEFINED;
	}
}

