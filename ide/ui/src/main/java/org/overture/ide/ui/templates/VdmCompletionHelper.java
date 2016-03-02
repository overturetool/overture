package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.overture.ast.node.INode;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;

public class VdmCompletionHelper {

	static VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	
	public boolean checkForDuplicates(String value, ArrayList<String> container){
		
		Iterator<String> iter = container.iterator();

		while(iter.hasNext()){
			String item = iter.next();
			if(Objects.equals(value, item)){
				return true;
			}
		}
		return false;
	}
	
	public boolean nullOrEmptyCheck(String str){
		return str != null && !str.isEmpty();
	}

	public boolean findInString(String text,String word)
	{
    	if(text == ""){
    		return true;
    	}
    	return word.toLowerCase().startsWith(text.toLowerCase());
	}
    
	public void createProposal(INode node, String displayname, String replacmentString,String additionalProposalInfo,final VdmCompletionContext info, 
    		final List<ICompletionProposal> proposals,final int offset)
    {
    	if(nullOrEmptyCheck(replacmentString) && findInString(info.proposalPrefix,replacmentString))
		{	
			IContextInformation contextInfo = new ContextInformation(displayname, displayname); //$NON-NLS-1$
			
			int curOffset = offset + info.offset;// - info2.proposalPrefix.length();
			int length = replacmentString.length();
			int replacementLength = info.proposalPrefix.length();
			
			proposals.add(new CompletionProposal(replacmentString, curOffset, replacementLength, length, imgProvider.getImageLabel(node, 0), displayname, contextInfo, additionalProposalInfo));
		}
    }
	
}
