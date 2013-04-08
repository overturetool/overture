package org.overture.ide.ui.editor.syntax;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class MultipleWordsWordRule extends WordRule
{

	public MultipleWordsWordRule(IWordDetector detector, IToken defaultToken,
			boolean ignoreCase)
	{
		super(detector, defaultToken, ignoreCase);
	}

	private StringBuffer fBuffer= new StringBuffer();
	private boolean fIgnoreCase= false;


	
	public void addWord(String word, IToken token) {
		Assert.isNotNull(word);
		Assert.isNotNull(token);

		// If case-insensitive, convert to lower case before adding to the map
		if (fIgnoreCase)
			word= word.toLowerCase();
		fWords.put(word, token);
	}
	
	
	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int c= scanner.read();
		if (c != ICharacterScanner.EOF && fDetector.isWordStart((char) c)) {
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {

				fBuffer.setLength(0);
				do {
					
					//If the current character is a space we might already have a multiple words keyword
					//This could be the case if we had read something like: "is subclass of"
					if(c == ' ')
					{		
						IToken token= readToken();

						if (token != null)
							return token;
					}
					
					fBuffer.append((char) c);					
					c= scanner.read();
				} while (c != ICharacterScanner.EOF && fDetector.isWordPart((char) c));
				scanner.unread();


				
				IToken token= readToken();

				if (token != null)
					return token;

				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);

				return fDefaultToken;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
	
	protected void unreadBuffer(ICharacterScanner scanner) {
		for (int i= fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}
	
	private IToken readToken()
	{
		String buffer= fBuffer.toString();
		// If case-insensitive, convert to lower case before accessing the map
		if (fIgnoreCase)
			buffer= buffer.toLowerCase();
		
		//One or more spaces should simply be replaced by a single space
		buffer = buffer.replaceAll("\\s+", " ");
		
		return (IToken)fWords.get(buffer);
	}

}
