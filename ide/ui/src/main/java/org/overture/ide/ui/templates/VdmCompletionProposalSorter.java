package org.overture.ide.ui.templates;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalSorter;

public class VdmCompletionProposalSorter implements ICompletionProposalSorter {

	@Override
	public int compare(ICompletionProposal arg0, ICompletionProposal arg1) {
		String functionName1 = arg0.getDisplayString().toUpperCase();
		String functionName2 = arg1.getDisplayString().toUpperCase();
		functionName1 = functionName1.replaceAll("[^a-zA-Z0-9]", "");
		functionName2 = functionName2.replaceAll("[^a-zA-Z0-9]", "");
		   //ascending order
		return functionName1.compareTo(functionName2);
		
	}

}
