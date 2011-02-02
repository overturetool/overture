package org.overture.ide.ui.editor.syntax;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class PrefixedUnderscoreRule implements IRule
{

	public static final int EOF = -1;

	IToken fToken = null;

	private String fPrefix;

	public PrefixedUnderscoreRule(String prefix, IToken token)
	{
		fToken = token;
		fPrefix = prefix;
	}

	public IToken evaluate(ICharacterScanner scanner)
	{
		StringBuffer sb = new StringBuffer();
		int c = -1;// scanner.read();
		// sb.append((char)c);
		// if (c == EOF) {
		// scanner.unread();
		// return Token.EOF;
		// }

		int readCount = 0;
		for (int i = 0; i < fPrefix.toCharArray().length; i++)
		{
			c = scanner.read();
			if (c == EOF)
			{
				scanner.unread();
				return Token.EOF;
			}
			sb.append((char) c);
			readCount++;
			if (c != fPrefix.toCharArray()[i])
			{

				for (int j = 0; j < readCount; j++)
				{
					scanner.unread();
				}

				return Token.UNDEFINED;

			}
		}

		if (readCount == fPrefix.length())
		{
			c = scanner.read();
			sb.append((char) c);
			readCount++;
			if (c == '_')
			{
				c = scanner.read();
				sb.append((char) c);
				scanner.unread();// we just need to check the next char not consume it
				if (Character.isJavaIdentifierStart(c))
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

		// if(readCount == fPrefix.length()-1)
		// {
		// // c = scanner.read();
		// // sb.append((char)c);
		// // readCount++;
		// if(c=='_')
		// {
		// c = scanner.read();
		// sb.append((char)c);
		// scanner.unread();//we just need to check the next char not consume it
		// if(Character.isJavaIdentifierStart(c))
		// {
		// return fToken;
		// }
		// }
		// }
		//		
		// for (int i = 0; i < readCount; i++)
		// {
		// scanner.unread();
		// }

		// return Token.UNDEFINED;

	}
}