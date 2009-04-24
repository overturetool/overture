/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
//import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
//import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.jface.text.rules.WordRule;
//import org.overturetool.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.EditorCoreConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureKeywords;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;
import org.overturetool.eclipse.plugins.editor.ui.scriptcolor.provider.IScriptColorProvider;
//import org.overturetool.internal.ui.rules.FloatNumberRule;

public class OvertureCodeScanner extends AbstractScriptScanner {

	private static String fgReturnKeyword = "return";
	private static String fgTokenProperties[] = new String[] {
			OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT,
			OvertureColorConstants.OVERTURE_DEFAULT,
			OvertureColorConstants.OVERTURE_KEYWORD,
			OvertureColorConstants.OVERTURE_KEYWORD_RETURN,
			OvertureColorConstants.OVERTURE_NUMBER,
			OvertureColorConstants.OVERTURE_FUNCTION_DEFINITION, 
			OvertureColorConstants.OVERTURE_TYPE,
			OvertureColorConstants.OVERTURE_OPERATOR,
			OvertureColorConstants.OVERTURE_CONSTANT,
			OvertureColorConstants.OVERTURE_FUNCTION,
			OvertureColorConstants.OVERTURE_PREDICATE

	};

	private static IScriptColorProvider[] providers;
	static {
		initProviders();
	}

	private static void initProviders() {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EditorCoreUIConstants.SCRIPT_COLOR_PROVIDER);  //"org.overturetool.ui.scriptcolor.provider"
		IExtension[] extensions = extensionPoint.getExtensions();
		
		ArrayList<Object> providerList = new ArrayList<Object>();
		for (int a = 0; a < extensions.length; a++) {
			IConfigurationElement[] configurationElements = extensions[a].getConfigurationElements();
			for (int b = 0; b < configurationElements.length; b++) {
				IConfigurationElement configurationElement = configurationElements[b];
				try {
					Object createExecutableExtension = configurationElement.createExecutableExtension("class");
					if (createExecutableExtension instanceof IScriptColorProvider) {
						providerList.add(createExecutableExtension);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
				// System.out.println(configurationElement.getName());
			}
		}
		IScriptColorProvider[] provider = new IScriptColorProvider[providerList.size()];
		providerList.toArray(provider);
		providers = provider;
	}

	public OvertureCodeScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
	}

	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	protected List createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		IToken keyword = getToken(OvertureColorConstants.OVERTURE_KEYWORD);
		IToken keywordReturn = getToken(OvertureColorConstants.OVERTURE_KEYWORD_RETURN);
		IToken other = getToken(OvertureColorConstants.OVERTURE_DEFAULT);
//		IToken def = getToken(OvertureColorConstants.OVERTURE_FUNCTION_DEFINITION);
//		IToken number = getToken(OvertureColorConstants.OVERTURE_NUMBER);
		// comments are already done by the TASK scanner...
		IToken comment = getToken(OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT);
		IToken commentMulti = getToken(OvertureColorConstants.OVERTURE_SINGLE_LINE_COMMENT);
		
		IToken operator = this.getToken(OvertureColorConstants.OVERTURE_OPERATOR);
//		IToken predicate = this.getToken(IOvertureColorConstants.OVERTURE_PREDICATE);
		IToken type = this.getToken(OvertureColorConstants.OVERTURE_TYPE);
		IToken constant = this.getToken(OvertureColorConstants.OVERTURE_CONSTANT);
		IToken function = this.getToken(OvertureColorConstants.OVERTURE_FUNCTION);
		
		// Add rule for single line comments.
		rules.add(new EndOfLineRule("--", comment));
//		
		rules.add(new MultiLineRule("/*","*/",commentMulti));

		// 	Add generic whitespace rule.
		rules.add(new WhitespaceRule(new OvertureWhitespaceDetector()));
		// Add word rule for keywords, types, and constants.
//		OvertureWordRule wordRule = new OvertureWordRule(new OvertureWordDetector(), other, null, def);
		WordRule wordRule  = new WordRule(new OvertureWordDetector(), other);
		
		// Predicate
		String[] predicates = OvertureKeywords.getPredicates();
		for (int i = 0; i < predicates.length; i++) {
			wordRule.addWord(predicates[i], keyword);
		}
		
		//type
		String[] types = OvertureKeywords.getTypes();
		for (int i = 0; i < types.length; i++) {
			wordRule.addWord(types[i], type);
		}
		
		// constants
		String[] constants = OvertureKeywords.getConstants();
		for (int i = 0; i < constants.length; i++) {
			wordRule.addWord(constants[i], constant);
		}
		
		// functions
		String[] functions = OvertureKeywords.getFunctions();
		for (int i = 0; i < functions.length; i++) {
			wordRule.addWord(functions[i], function);
		}
		
		// operator
		String[] operators = OvertureKeywords.getTypes();
		for (int i = 0; i < operators.length; i++) {
			wordRule.addWord(operators[i], operator);
		}
//		WordPatternRule multiRule = new WordPatternRule(OvertureWordDetector,)
		
		// reservedWords
		String[] reservedWords = OvertureKeywords.getReservedwords();
		for (int i = 0; i < reservedWords.length; i++) {
			wordRule.addWord(reservedWords[i], keyword);
		}
		
//		String[] multi = OvertureKeywords.getMultipleKeywords();
//		for (int i = 0; i < multi.length; i++) {
//			wordRule.addWord(multi[i], keyword);
//		}
		
		wordRule.addWord(fgReturnKeyword, keywordReturn);

		for (int i = 0; i < providers.length; i++) {
			String[] keywords = providers[i].getKeywords();
			if (keywords != null) {
				for (int j = 0; j < keywords.length; j++) {
					wordRule.addWord(keywords[j], providers[i].getToken(keywords[j]));
				}
			}
		}
		rules.add(wordRule);
		
//		rules.add(new FloatNumberRule(number));

		
		for (int i = 0; i < providers.length; i++) {
			IRule[] r = providers[i].getRules();
			if (r != null) {
				for (int j = 0; j < r.length; j++) {
					rules.add(r[j]);
				}
			}
		}
		setDefaultReturnToken(other);
		return rules;
	}
}
