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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * Rule scanner for multiword words like "is subclass responsibility".
 * 
 * @author kel
 */
public class MultipleWordsWordRule extends WordRule
{
	/**
	 * Used as result for {@link MultipleWordsWordRule#checkMatch(String)} to hold the token and unmatched length
	 * 
	 * @author kel
	 */
	private static class WordMatch
	{
		public final IToken token;
		public final Integer unMatchedLength;

		public WordMatch(IToken token, Integer unMatchedLength)
		{
			this.token = token;
			this.unMatchedLength = unMatchedLength;
		}
	}

	public MultipleWordsWordRule(IWordDetector detector, IToken defaultToken,
			boolean ignoreCase)
	{
		super(detector, defaultToken, ignoreCase);
	}

	private StringBuffer fBuffer = new StringBuffer();
	private boolean fIgnoreCase = false;

	@SuppressWarnings("unchecked")
	public void addWord(String word, IToken token)
	{
		Assert.isNotNull(word);
		Assert.isNotNull(token);

		// If case-insensitive, convert to lower case before adding to the map
		if (fIgnoreCase)
		{
			word = word.toLowerCase();
		}
		fWords.put(word, token);
	}

	/**
	 * Calculates the maximum number of parts in the largest word in the rule scanner
	 * 
	 * @return the largest number of parts. (number of spaces spaced words"
	 */
	private int getMaxPartCount()
	{
		int max = 0;
		for (Object k : super.fWords.keySet())
		{
			String key = k.toString();
			int count = key.split("\\s+?").length;
			if (count > max)
			{
				max = count;
			}
		}
		return max;
	}
	
	int offset = 0;
	
	private int read(ICharacterScanner scanner){
		offset++;
		return scanner.read();
	}
	
	private void unread(ICharacterScanner scanner){
		offset--;
		 scanner.unread();
	}
	private IToken returnEmpty(IToken token)
	{
		if(offset!=0)
		{
			System.out.println("Something is wrong: "+offset);
		}
		return token;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		offset = 0;
		int c = read(scanner);
		if (c != ICharacterScanner.EOF && fDetector.isWordStart((char) c))
		{
			if (fColumn == UNDEFINED || fColumn == scanner.getColumn() - 1)
			{
				fBuffer.setLength(0);
				fBuffer.append((char) c);

				for (int i = 0; i < getMaxPartCount(); i++)
				{
					// read a word for each part
					do
					{
						c = read(scanner);
						fBuffer.append((char) c);
						// System.out.println("Read: '"+c + "' - '"+(char)c+"'");
					} while (c != ICharacterScanner.EOF && c != ' '
							&& fDetector.isWordStart((char) c));
				}

				// we may have read EOF so unread it
				popEof(scanner);

				final char lastChar = fBuffer.charAt(fBuffer.length()-1);
				if (lastChar==' ' || lastChar=='\t'
						|| lastChar=='\n' || lastChar=='\r')
				{
					unread(scanner);// last space
					fBuffer.delete(fBuffer.length() - 1, fBuffer.length());
				}
				
				String text = fBuffer.toString().toString().replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
				String key = text;// text.substring(0, text.length() - 1);


				WordMatch match = checkMatch(key);
				if (match != null)
				{
					if (match.unMatchedLength > 0)
					{
						// unread unmatched parts
						for (int i = 0; i < match.unMatchedLength; i++)
						{
							unread(scanner);
						}
					}

					return match.token;
				}

				if (fDefaultToken.isUndefined())
				{
					unreadBuffer(scanner);
				}

				return returnEmpty(fDefaultToken);

			}
		}

		unread(scanner);
		return returnEmpty(Token.UNDEFINED);
	}

	private void popEof(ICharacterScanner scanner)
	{
		while (fBuffer.length() > 0
				&& fBuffer.charAt(fBuffer.length() - 1) == (char) -1)
		{
			unread(scanner);
			fBuffer.delete(fBuffer.length() - 1, fBuffer.length());
		}
	}

	/**
	 * Checks if the scanned multipart word exists in the {@code fWords} maps keys collection os if the prefix exists.
	 * 
	 * @param key
	 * @return
	 */
	private WordMatch checkMatch(String key)
	{
		String matchString = key;

		while (matchString.indexOf(' ') > -1)
		{
			if (fWords.containsKey(matchString))
			{
//				System.out.println("key '"+key+"' not matched: "+(key.length()
//						- matchString.length()));
				return new WordMatch((IToken) fWords.get(matchString), key.length()
						- matchString.length());
			}

			matchString = matchString.substring(0, matchString.lastIndexOf(' '));
		}

		return null;
	}

	protected void unreadBuffer(ICharacterScanner scanner)
	{
//		System.out.println("Did not match '"+fBuffer+"' " + fBuffer.length());
		for (int i = fBuffer.length() - 1; i >= 0; i--)
		{
			unread(scanner);
//			System.out.println("Unread");
		}
	}

}
