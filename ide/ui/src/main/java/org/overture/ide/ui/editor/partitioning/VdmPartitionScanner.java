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
package org.overture.ide.ui.editor.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class VdmPartitionScanner extends RuleBasedPartitionScanner {

	// string constants for different partition types
	public final static String MULTILINE_COMMENT = "__vdm_multiline_comment";
	public final static String SINGLELINE_COMMENT = "__vdm_singleline_comment";
	public final static String STRING = "__vdm_string";
	public final static String LATEX = "__vdm_latex";
	
	public final static String[] PARTITION_TYPES = new String[] {
			MULTILINE_COMMENT, SINGLELINE_COMMENT, STRING, LATEX };

	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public VdmPartitionScanner() { 
		super(); 
		IToken multilinecomment= new Token(MULTILINE_COMMENT); 
		IToken singlelinecomment= new Token(SINGLELINE_COMMENT); 
		IToken string = new Token(STRING); 
//		IToken latex = new Token(LATEX);
		
		List<PatternRule> rules= new ArrayList<PatternRule>(); 
		// Add rule for single line comments. 
		rules.add(new EndOfLineRule("--", singlelinecomment)); 
		// Add rule for strings and character constants. 
		rules.add(new SingleLineRule("\"", "\"", string, '\\')); 
		rules.add(new SingleLineRule("'", "'", string, '\\')); 
		// Add rules for multi-line comments and javadoc. 
		rules.add(new MultiLineRule("/*", "*/", multilinecomment, (char) 0, true)); 



		
		IPredicateRule[] result= new IPredicateRule[rules.size()]; 
		rules.toArray(result); 
		setPredicateRules(result); 
	}
	
}
