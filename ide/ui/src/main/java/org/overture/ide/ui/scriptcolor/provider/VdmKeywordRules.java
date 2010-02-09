package org.overture.ide.ui.scriptcolor.provider;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.overture.ide.ui.partitioning.IVdmColorConstants;
import org.overture.ide.ui.partitioning.IVdmKeywords;
import org.overture.ide.ui.partitioning.VdmWhitespaceDetector;
import org.overture.ide.ui.partitioning.VdmWordDetector;



public class VdmKeywordRules implements IScriptColorProvider
{
	public VdmKeywordRules()
	{
		// TODO Auto-generated constructor stub
	}
	public VdmKeywordRules(IVdmKeywords keywords)
	{
		this.keywords=keywords;
	}
private  IVdmKeywords keywords=null;
	private static String fgReturnKeyword = "return";
	private static List<String> fgTokenProperties = new Vector<String>();
	static 
	{
		fgTokenProperties.add(IVdmColorConstants.VDM_COMMENT);
		fgTokenProperties.add(IVdmColorConstants.VDM_DEFAULT);
		fgTokenProperties.add(IVdmColorConstants.VDM_KEYWORD);
		fgTokenProperties.add(IVdmColorConstants.VDM_KEYWORD_RETURN);
		fgTokenProperties.add(IVdmColorConstants.VDM_NUMBER);
		fgTokenProperties.add(IVdmColorConstants.VDM_FUNCTION_DEFINITION);
		fgTokenProperties.add(IVdmColorConstants.VDM_TYPE);
		fgTokenProperties.add(IVdmColorConstants.VDM_OPERATOR);
		fgTokenProperties.add(IVdmColorConstants.VDM_CONSTANT);
		fgTokenProperties.add(IVdmColorConstants.VDM_FUNCTION);
		fgTokenProperties.add(IVdmColorConstants.VDM_PREDICATE);
 
	};

	/**
	 * Add word rule for keywords, types, and constants.
	 * 
	 * @param wordRule
	 * @return
	 */
	private  IToken setVdmWordDetection(
			IScriptColorTokenLocator tokenLocater, WordRule wordRule)
	{

		IToken keyword = tokenLocater.getColorToken(IVdmColorConstants.VDM_KEYWORD);
		IToken keywordReturn = tokenLocater.getColorToken(IVdmColorConstants.VDM_KEYWORD_RETURN);
		IToken operator = tokenLocater.getColorToken(IVdmColorConstants.VDM_OPERATOR);
		IToken type = tokenLocater.getColorToken(IVdmColorConstants.VDM_TYPE);
//		IToken constant = tokenLocater.getColorToken(IVdmColorConstants.VDM_CONSTANT);
//		IToken function = tokenLocater.getColorToken(IVdmColorConstants.VDM_FUNCTION);
		// Predicate
		String[] predicates = keywords.getBinaryoperators();
		setWordDetection(keyword, wordRule, predicates);

		// type
		String[] types = keywords.getBasictypes();
		setWordDetection(type, wordRule, types);

		// constants
		String[] constants = keywords.getBinaryoperators();
		setWordDetection(keyword, wordRule, constants);

		// functions
		String[] functions = keywords.getTextvalues();
		setWordDetection(keyword, wordRule, functions);

		// operator
		String[] operators = keywords.getUnaryoperators();
		setWordDetection(operator, wordRule, operators);

		// reservedWords
		String[] reservedWords = keywords.getReservedwords();
		setWordDetection(keyword, wordRule, reservedWords);
		
		// reservedWords
		String[] historyCounters = keywords.getHistoryCounters();
		setWordDetection(keyword, wordRule, historyCounters);
		return keywordReturn;
	}

	private static void setWordDetection(IToken colorToken, WordRule wordRule,
			String[] words)
	{
		for (int i = 0; i < words.length; i++)
		{
			wordRule.addWord(words[i], colorToken);
		}
	}

	/**
	 * get rules for comments.
	 * 
	 * @return
	 */
	private static Collection<IRule> getCommentRules(
			IScriptColorTokenLocator tokenLocater)
	{
		List<IRule> rules = new Vector<IRule>();
		IToken comment = tokenLocater.getColorToken(IVdmColorConstants.VDM_COMMENT);
		IToken commentMulti = tokenLocater.getColorToken(IVdmColorConstants.VDM_COMMENT);
		rules.add(new EndOfLineRule("--", comment));
		//	
		rules.add(new MultiLineRule("/*", "*/", commentMulti));
	//	rules.add(new MultiLineRule("\\begin{", "\\end{", commentMulti));

		rules.add(new MultiLineRule("\\end{vdm_al}", "\\begin{vdm_al}",  commentMulti));
		//rules.add(new EndOfLineRule("\\", comment)); 
		return rules;
	}

	public List<IRule> getRules(IScriptColorTokenLocator tokenLocater)
	{
		List<IRule> rules = new ArrayList<IRule>();
		rules.addAll(getCommentRules(tokenLocater));
		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new VdmWhitespaceDetector()));

		IToken other = tokenLocater.getColorToken(IVdmColorConstants.VDM_DEFAULT);

		WordRule wordRule = new WordRule(new VdmWordDetector(), other);

		// Add word rule for keywords, types, and constants.
		IToken keywordReturn = setVdmWordDetection(tokenLocater, wordRule);

		// String[] multi = OvertureKeywords.getMultipleKeywords();
		// for (int i = 0; i < multi.length; i++) {
		// wordRule.addWord(multi[i], keyword);
		// }

		wordRule.addWord(fgReturnKeyword, keywordReturn);
		rules.add(wordRule);
		return rules;
	}

	public List<String> getTokenProperties()
	{
		// TODO Auto-generated method stub
		return fgTokenProperties;
	}

}
