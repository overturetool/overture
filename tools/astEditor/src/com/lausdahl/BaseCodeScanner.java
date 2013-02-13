package com.lausdahl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public abstract class BaseCodeScanner extends RuleBasedScanner
{
	public static class ContractWordDetector implements IWordDetector
	{

		/*
		 * (non-Javadoc) Method declared on IWordDetector.
		 */
		public boolean isWordPart(char character)
		{
			return Character.isJavaIdentifierPart(character);
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector.
		 */
		public boolean isWordStart(char character)
		{
			return Character.isJavaIdentifierStart(character)
					|| isVdmIdentifierStart(character);
		}

		private boolean isVdmIdentifierStart(char character)
		{
			boolean isIdentifier = false;
			switch (character)
			{
				case '#':
				case '<':
					isIdentifier = true;

				default:

			}
			return isIdentifier;
		}

	}

	public static class WhitespaceDetector implements IWhitespaceDetector
	{

		public boolean isWhitespace(char c)
		{

			return Character.isWhitespace(c);
		}

	}

	public BaseCodeScanner(ColorProvider provider)
	{
		setup(provider);
	}

	protected void setup(ColorProvider provider)
	{
		IToken type = new Token(new TextAttribute(provider.getColor(new RGB(0, 0, 192)), null, SWT.BOLD));
		IToken keyword = new Token(new TextAttribute(provider.getColor(ColorProvider.KEYWORD), null, SWT.BOLD));
		IToken string = new Token(new TextAttribute(provider.getColor(ColorProvider.STRING)));
		IToken comment = new Token(new TextAttribute(provider.getColor(ColorProvider.SINGLE_LINE_COMMENT)));
		IToken other = new Token(new TextAttribute(provider.getColor(ColorProvider.DEFAULT)));
		IToken graph = new Token(new TextAttribute(provider.getColor(ColorProvider.GRAPH)));

		List<IRule> rules = new ArrayList<IRule>();
		// Add rule for single line comments.
//		rules.add(new EndOfLineRule("--", comment));
		rules.add(new EndOfLineRule("//", comment));
		// Multi line comment
		rules.add(new MultiLineRule("/*", "*/", comment));

		// Add rule for strings.
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		rules.add(new SingleLineRule("(", ")", graph, '\\'));
		rules.add(new SingleLineRule("[", "]", keyword, '\\'));
		rules.add(new SingleLineRule("{", "}", type, '\\'));
		// rules.add(new SingleLineRule("{->", "}", comment, '\\'));
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		// Add word rule for keywords.
		WordRule wordRule = new WordRule(new ContractWordDetector(), other);

		for (String keywd : getKeywords())
		{
			wordRule.addWord(keywd, keyword);
		}

		for (String typeName : getTypeWords())
		{
			wordRule.addWord(typeName, type);
		}

		for (String word : getCommentWords())
		{
			wordRule.addWord(word, comment);
		}

		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	protected abstract String[] getKeywords();

	protected abstract String[] getCommentWords();

	protected abstract String[] getTypeWords();

}
