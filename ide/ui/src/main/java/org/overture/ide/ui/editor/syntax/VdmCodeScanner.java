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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
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

		final IToken stringBold = new Token(new TextAttribute(provider.getColor(VdmColorProvider.DEFAULT), null, SWT.BOLD
				| SWT.ITALIC));
		IToken comment = new Token(new TextAttribute(provider.getColor(VdmColorProvider.SINGLE_LINE_COMMENT)));
		IToken latex = new Token(new TextAttribute(provider.getColor(VdmColorProvider.LATEX)));
		final IToken other = new Token(new TextAttribute(provider.getColor(VdmColorProvider.DEFAULT)));

		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new VdmWhitespaceDetector()));

		// TODO: this is a hack to get latex related stuff commented
		//rules.add(new SingleLineRule("\\begin{vdm_al", "}", comment));
		//rules.add(new SingleLineRule("\\end{vdm_al", "}", comment));
		rules.add(new MultiLineRule("\\end{vdm_al}","\\begin{vdm_al}", latex,(char) 0,true));
		rules.add(new MultiLineRule("\\section{","\\begin{vdm_al}", latex,(char) 0,false));
//		rules.add(new SingleLineRule("\\end{vdm_al", "}", comment,));

		if (fgKeywords.supportsQuoteTypes())
		{
			rules.add(new QuoteRule(type));
		}

		if (fgKeywords.supportsTypleSelect())
		{
			rules.add(new TupleSelectRule(stringBold));
		}

		for (String prefix : fgKeywords.getUnderscorePrefixKeywords())
		{
			rules.add(new PrefixedUnderscoreRule(prefix, keyword));
		}

		for (String prefix : fgKeywords.getUnderscorePrefixReservedWords())
		{
			rules.add(new PrefixedUnderscoreRule(prefix, stringBold));
		}

		MultipleWordsWordRule multipleWordRule = new MultipleWordsWordRule(new VdmWordDetector(), Token.UNDEFINED, false);
		for (int i = 0; i < fgKeywords.getMultipleKeywords().length; i++)
		{
			multipleWordRule.addWord(fgKeywords.getMultipleKeywords()[i], keyword);
		}
		rules.add(multipleWordRule);

		// Add word rule for keywords.
		WordRule wordRule = new WordRule(new VdmWordDetector(), other);// Not sure why Token.UNDEFINED doesn't
																					// work but
		// it makes S'end' colored.

		for (int i = 0; i < fgKeywords.getAllSingleWordKeywords().length; i++)
		{
			wordRule.addWord(fgKeywords.getAllSingleWordKeywords()[i], keyword);
		}
		rules.add(wordRule);

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
		// sets the default style. If styledText.getStyleRangeAtOffset is called on the editor this default style is
		// returned instead of null
		setDefaultReturnToken(other);
	}

//	/**
//	 * overrides super method to debug rule scanners
//	 */
//	public IToken nextToken()
//	{
//
//		fTokenOffset = fOffset;
//		fColumn = UNDEFINED;
//
//		if (fRules != null)
//		{
//			for (int i = 0; i < fRules.length; i++)
//			{
//				int o = fOffset;
//
//				IToken token = (fRules[i].evaluate(this));
//				if (o != fOffset)
//				{
//					try
//					{
//
//						String text = fDocument.get(o, fOffset - o);
//						System.out.println("Offset changed from: " +o+" to: "+ fOffset
//								+ ", scanned '" + text + "' with rule "
//								+ fRules[i].getClass().getSimpleName()
//								+ " is token undefined: "+( token.isUndefined()?"yes":"no"));
//					} catch (BadLocationException e)
//					{
//						e.printStackTrace();
//					}
//				}
//
//				if (!token.isUndefined())
//					return token;
//			}
//		}
//
//		if (read() == EOF)
//			return Token.EOF;
//		return fDefaultReturnToken;
//	}

	protected abstract IVdmKeywords getKeywords();
}
