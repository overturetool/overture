package org.overture.ide.vdmpp.ui.internal.partitioning;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.overture.ide.vdmpp.ui.internal.editor.IVdmPpPartitions;



public class VdmPpPartitionScanner extends RuleBasedPartitionScanner {

	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public VdmPpPartitionScanner() {
		super();

		IToken string = new Token(IVdmPpPartitions.VDMPP_STRING);
		IToken comment = new Token(IVdmPpPartitions.VDMPP_COMMENT);
		IToken doc = new Token(IVdmPpPartitions.VDMPP_DOC);

		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		rules.add(new MultiLineRule("\"", "\"", string, '\\'));
		rules.add(new MultiLineRule("/**", "*/", doc)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("/*", "*/", comment)); //$NON-NLS-1$ //$NON-NLS-2$

		//	rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$		
		// Add rule for character constants.
		//		rules.add(new SingleLineRule("'", "'", string, '\\'));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
	
	
}