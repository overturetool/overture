package org.overture.ide.ui.editor.syntax;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

public abstract class VdmCodeScanner extends RuleBasedScanner
{

	private IVdmKeywords fgKeywords = getKeywords();

	public static final String[] latexOperators = { "\\begin{vdm_al}",
			"\\end{vdm_al}" };

	public VdmCodeScanner(VdmColorProvider provider)
	{

		IToken keyword = new Token(new TextAttribute(provider.getColor(VdmColorProvider.KEYWORD), null, SWT.BOLD));
		IToken type = new Token(new TextAttribute(provider.getColor(VdmColorProvider.TYPE), null, SWT.BOLD));
		final IToken string = new Token(new TextAttribute(provider.getColor(VdmColorProvider.STRING)));
		final IToken stringBold = new Token(new TextAttribute(provider.getColor(VdmColorProvider.DEFAULT),null, SWT.BOLD | SWT.ITALIC));
		IToken comment = new Token(new TextAttribute(provider.getColor(VdmColorProvider.SINGLE_LINE_COMMENT)));
		 final IToken other = new Token(new TextAttribute(provider.getColor(VdmColorProvider.DEFAULT)));
		
		List<IRule> rules = new ArrayList<IRule>();
		// Add rule for single line comments.
		rules.add(new EndOfLineRule("--", comment));
		// Multi line comment
		rules.add(new MultiLineRule("/*", "*/", comment));

		// Add rule for strings.
		rules.add(new CharacterRule(string));
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		// rules.add(new SingleLineRule("'", "'", string, '\\'));
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new VdmWhitespaceDetector()));

		// TODO: this is a hack to get latex related stuff commented
		rules.add(new SingleLineRule("\\begin{vdm_al", "}", comment));
		rules.add(new SingleLineRule("\\end{vdm_al", "}", comment));

		if(fgKeywords.supportsQuoteTypes())
		{
			rules.add(new WordPatternRule(new QuoteWordDetector(), "<", ">", type));
		}
		
		if(fgKeywords.supportsTypleSelect())
		{
			rules.add(new TupleSelectRule(stringBold));
		}
		
		for (String prefix : fgKeywords.getUnderscorePrefixKeywords())
		{
			rules.add(new PrefixedUnderscoreRule(prefix,keyword));
		}
	
		for (String prefix : fgKeywords.getUnderscorePrefixReservedWords())
		{
			rules.add(new PrefixedUnderscoreRule(prefix,stringBold));
		}
		
		WordRule wordRuleWithSpaces = new WordRule(new VdmMultipleWordDetector(), Token.UNDEFINED);
		for (int i = 0; i < fgKeywords.getMultipleKeywords().length; i++)
		{
			wordRuleWithSpaces.addWord(fgKeywords.getMultipleKeywords()[i], keyword);
		}
		rules.add(wordRuleWithSpaces);

		// Add word rule for keywords.
		WordRule wordRule = new WordRule(new VdmWordDetector(), other);//Not sure why Token.UNDEFINED doesn't work but it makes S'end' colored.

		for (int i = 0; i < fgKeywords.getAllSingleWordKeywords().length; i++)
		{
			wordRule.addWord(fgKeywords.getAllSingleWordKeywords()[i], keyword);
		}
		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);

	}

	private static class VdmMultipleWordDetector extends VdmWordDetector
	{
		@Override
		public boolean isWordPart(char character)
		{
			return super.isWordPart(character) || character == ' ';
		}
	}

	private static class QuoteWordDetector implements IWordDetector
	{

		public boolean isWordPart(char c)
		{
			return Character.isJavaIdentifierPart(c) || c == '>';
		}

		public boolean isWordStart(char c)
		{
			return '<' == c;
		}

	}

	
	protected abstract IVdmKeywords getKeywords();
}
