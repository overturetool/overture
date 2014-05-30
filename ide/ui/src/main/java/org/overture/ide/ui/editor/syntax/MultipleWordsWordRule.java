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

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		int c = scanner.read();
		char charRead = (char) c;
		if (c != ICharacterScanner.EOF && fDetector.isWordStart(charRead))
		{
			if (fColumn == UNDEFINED || fColumn == scanner.getColumn() - 1)
			{
				fBuffer.setLength(0);
				boolean abort = false;
				fBuffer.append(charRead);

				for (int i = 0; i < getMaxPartCount(); i++)
				{
					// read a word for each part
					do
					{
						c = scanner.read();
						fBuffer.append((char) c);
						System.out.println("Read: '"+c + "' - '"+(char)c+"'");
					} while (c != ICharacterScanner.EOF && c != ' '
							&& fDetector.isWordStart(charRead));
				}
				
				//we may have read EOF so unread it
				while(fBuffer.length()>0 && fBuffer.charAt(fBuffer.length()-1)==(char)-1)
				{
					fBuffer.delete(fBuffer.length()-1, fBuffer.length());
				}

				String text = fBuffer.toString().toString().replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
				String key = text;//text.substring(0, text.length() - 1);
				
				if(key.endsWith(" ")||key.endsWith("\t")||key.endsWith("\n")||key.endsWith("\r"))
				{
					scanner.unread();// last space
					fBuffer.delete(fBuffer.length()-1, fBuffer.length());
					key = key.substring(0,key.length()-1);
				}

				if (!abort)
				{
					WordMatch match = checkMatch(key);
					if (match != null)
					{
						if (match.unMatchedLength > 0)
						{
							// unread unmatched parts
							for (int i = 0; i < match.unMatchedLength; i++)
							{
								scanner.unread();
							}
						}
						
						return match.token;
					}
				}

				unreadBuffer(scanner);

				return Token.UNDEFINED;

			}
		}

		scanner.unread();
		return Token.UNDEFINED;
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
				return new WordMatch((IToken) fWords.get(matchString), key.length()
						- matchString.length());
			}

			matchString = matchString.substring(0, matchString.lastIndexOf(' '));
		}

		return null;
	}

	protected void unreadBuffer(ICharacterScanner scanner)
	{
		for (int i = fBuffer.length() - 1; i >= 0; i--)
		{
			scanner.unread();
		}
	}

}
