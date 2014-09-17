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

public class PrefixedUnderscoreRule implements IRule
{
	public static final int EOF = -1;

	IToken fToken = null;

	final private char[] fPrefix;
	final private int fLength;

	public PrefixedUnderscoreRule(String prefix, IToken token)
	{
		fToken = token;
		fPrefix = prefix.toCharArray();
		fLength = fPrefix.length;
	}

	public IToken evaluate(ICharacterScanner scanner)
	{
		StringBuffer sb = new StringBuffer();
		int c = -1;

		int readCount = 0;
		for (int i = 0; i < fLength; i++)
		{
			c = scanner.read();
			if (c == EOF)
			{
				scanner.unread();
				return Token.EOF;
			}
			sb.append((char) c);
			readCount++;
			if (c != fPrefix[i])
			{

				for (int j = 0; j < readCount; j++)
				{
					scanner.unread();
				}

				return Token.UNDEFINED;

			}
		}

		if (readCount == fLength)
		{
			c = scanner.read();
			sb.append((char) c);
			readCount++;
			if (c == '_')
			{
				c = scanner.read();
				sb.append((char) c);
				scanner.unread();// we just need to check the next char not consume it
				if (Character.isJavaIdentifierStart(c)||Character.isWhitespace(c)||c=='(')
				{
					return fToken;
				}
			}
		}

		for (int j = 0; j < readCount; j++)
		{
			scanner.unread();
		}

		return Token.UNDEFINED;
	}
}
