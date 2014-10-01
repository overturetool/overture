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
package org.overture.ide.ui.editor.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class VdmPartitionScanner extends RuleBasedPartitionScanner implements IVdmPartitions{

	/**
	 * Detector for empty comments.
	 */
	static class EmptyCommentDetector implements IWordDetector {

		/*
		 * @see IWordDetector#isWordStart
		 */
		public boolean isWordStart(char c) {
			return (c == '/');
		}

		/*
		 * @see IWordDetector#isWordPart
		 */
		public boolean isWordPart(char c) {
			return (c == '*' || c == '/');
		}
	}


	/**
	 * Word rule for empty comments.
	 */
	static class EmptyCommentRule extends WordRule implements IPredicateRule {

		private IToken fSuccessToken;
		/**
		 * Constructor for EmptyCommentRule.
		 * @param successToken the token returned for success
		 */
		public EmptyCommentRule(IToken successToken) {
			super(new EmptyCommentDetector());
			fSuccessToken= successToken;
			addWord("/**/", fSuccessToken); //$NON-NLS-1$
		}

		/*
		 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
		 */
		public IToken evaluate(ICharacterScanner scanner, boolean resume) {
			return evaluate(scanner);
		}

		/*
		 * @see IPredicateRule#getSuccessToken()
		 */
		public IToken getSuccessToken() {
			return fSuccessToken;
		}
	}

	
	public final static String[] PARTITION_TYPES = new String[] {
			MULTILINE_COMMENT, SINGLELINE_COMMENT, STRING };

	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public VdmPartitionScanner() { 
		super(); 
		IToken multilinecomment= new Token(MULTILINE_COMMENT); 
		IToken singlelinecomment= new Token(SINGLELINE_COMMENT); 
		IToken string = new Token(STRING); 
//		IToken latex = new Token(LATEX);
		
		List<IPredicateRule> rules= new ArrayList<IPredicateRule>();
		// Add rule for single line comments. 
		rules.add(new EndOfLineRule("--", singlelinecomment)); 
		// Add rule for strings and character constants. 
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		//rules.add(new SingleLineRule("'", "'", string, '\\'));
		rules.add(new VdmCharRule());
		// Add rules for multi-line comments and javadoc. 
		rules.add(new MultiLineRule("/*", "*/", multilinecomment)); 
		
		
		// Add special case word rule.
		EmptyCommentRule wordRule= new EmptyCommentRule(multilinecomment);
		rules.add(wordRule);
		

		IPredicateRule[] result= new IPredicateRule[rules.size()]; 
		rules.toArray(result); 
		setPredicateRules(result); 
	}
	
}
