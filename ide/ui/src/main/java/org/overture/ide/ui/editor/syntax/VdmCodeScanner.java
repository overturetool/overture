package org.overture.ide.ui.editor.syntax;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;


public abstract class VdmCodeScanner extends RuleBasedScanner {

	private String[] fgKeywords = getKeywords();

	public static final String[] latexOperators = {
		  "\\begin{vdm_al}",
		  "\\end{vdm_al}"
	  };
	
	
	public VdmCodeScanner(VdmColorProvider provider) {

		IToken keyword = new Token(new TextAttribute(provider.getColor(VdmColorProvider.KEYWORD),null,SWT.BOLD));
		IToken string = new Token(new TextAttribute(provider.getColor(VdmColorProvider.STRING)));
		IToken comment = new Token(new TextAttribute(provider.getColor(VdmColorProvider.SINGLE_LINE_COMMENT)));
		IToken other = new Token(new TextAttribute(provider.getColor(VdmColorProvider.DEFAULT)));
		
		List<IRule> rules = new ArrayList<IRule>();
		// Add rule for single line comments.
		rules.add(new EndOfLineRule("--", comment));
		
		// Add rule for strings.
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new VdmWhitespaceDetector()));
		// Add word rule for keywords.
		WordRule wordRule = new WordRule(new VdmWordDetector(), other);
		
		//TODO: this is a hack to get latex related stuff commented
		rules.add(new SingleLineRule("\\begin{vdm_al","}", comment));
		rules.add(new SingleLineRule("\\end{vdm_al","}", comment));
		
		for (int i = 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], keyword);
		
		
			
		rules.add(wordRule);
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}
	
	protected  abstract String[] getKeywords();
	
	
	
}
