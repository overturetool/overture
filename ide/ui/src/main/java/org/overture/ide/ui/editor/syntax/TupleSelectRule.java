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
			
			boolean followedBySpace = Character.isSpace((char)c);
			
			
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

