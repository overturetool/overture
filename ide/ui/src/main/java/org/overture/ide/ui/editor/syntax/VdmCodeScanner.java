/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.editor.syntax;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
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
		
		final IToken stringBold = new Token(new TextAttribute(provider.getColor(VdmColorProvider.DEFAULT),null, SWT.BOLD | SWT.ITALIC));
		IToken comment = new Token(new TextAttribute(provider.getColor(VdmColorProvider.SINGLE_LINE_COMMENT)));
		 final IToken other = new Token(new TextAttribute(provider.getColor(VdmColorProvider.DEFAULT)));
		
		List<IRule> rules = new ArrayList<IRule>();
	

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
		
//		WordRule wordRuleWithSpaces = new MultipleWordsWordRule(new VdmMultipleWordDetector(), Token.UNDEFINED, false);
//		for (int i = 0; i < fgKeywords.getMultipleKeywords().length; i++)
//		{
//			wordRuleWithSpaces.addWord(fgKeywords.getMultipleKeywords()[i], keyword);
//		}
//		rules.add(wordRuleWithSpaces);

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
