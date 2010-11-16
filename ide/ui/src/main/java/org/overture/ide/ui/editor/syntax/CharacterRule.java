package org.overture.ide.ui.editor.syntax;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CharacterRule implements IRule {

	public static final int EOF = -1;

	IToken fToken = null;

	public CharacterRule(IToken token) {
		fToken = token;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		if (c == EOF) {
			scanner.unread();
			return Token.EOF;
		}

		if ((char) c == '\'') {

			c = scanner.read();
			if (c == EOF) {
				scanner.unread();
				return Token.EOF;
			}

			c = scanner.read();
			if ((char) c == '\'') {
				return fToken;
			}

		}

		scanner.unread();
		return Token.UNDEFINED;

	}

}
