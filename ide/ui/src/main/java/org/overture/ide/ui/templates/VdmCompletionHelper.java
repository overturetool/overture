package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.overture.ast.node.INode;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;

public class VdmCompletionHelper  extends VdmTemplateAssistProcessor{

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
    	if(nullOrEmptyCheck(replacmentString) && findInString(info.getProposalPrefix(),replacmentString))
		{	
			IContextInformation contextInfo = new ContextInformation(displayname, displayname); //$NON-NLS-1$
			
			int curOffset = offset + info.getReplacementOffset();// - info2.proposalPrefix.length();
			int length = replacmentString.length();
			int replacementLength = info.getProposalPrefix().length();
			
			proposals.add(new CompletionProposal(replacmentString, curOffset, replacementLength, length, imgProvider.getImageLabel(node, 0), displayname, contextInfo, additionalProposalInfo));
		}
    }

	public String[] templatePatternGenerator(List<String> extractedNames,String[] functionName){
		StringBuilder sbPattern = new StringBuilder();
		StringBuilder sbDisplayName = new StringBuilder();
		sbPattern.append(functionName[1]);
		sbDisplayName.append(functionName[1]);
		if((extractedNames != null && !extractedNames.isEmpty())){
			
			for (int i = 0; i < extractedNames.size(); i++) {
				String str = extractedNames.get(i);
			
				if(str != extractedNames.get(0)){
					sbPattern.append(", ");
					sbDisplayName.append(", ");
				}
				sbPattern.append("${" + str + "}");
				sbDisplayName.append(str);
			}
			
		}
		sbPattern.append(")");
		sbDisplayName.append(")");
		
		functionName[1] = sbPattern.toString();
		functionName[0] = sbDisplayName.toString();

    	return functionName;
	}
	
	public void dynamicTemplateCreator(String[] extractedName, String type, int offset,TemplateContext context,List<ICompletionProposal> proposals,VdmCompletionContext info,ITextViewer viewer,int nodeOffsetPosition) {
		
		if (context == null)
			return;
		
		if(nullOrEmptyCheck(extractedName[0]) && findInString(info.getProposalPrefix(),extractedName[0])){

			ITextSelection selection = (ITextSelection) viewer
					.getSelectionProvider().getSelection();
			// get caret pos = selection.getOffset()
				
			if (selection.getOffset() == offset){
				offset = selection.getOffset() + selection.getLength();
			}
			String prefix = extractPrefix(viewer, offset);
			Region region = new Region(offset - prefix.length(), prefix.length());

			context.setVariable("selection", selection.getText());
			
			Template template = new Template(extractedName[0],type,"org.overture.ide.vdmsl.ui.contextType",extractedName[1],true);
			
			proposals.add(createProposal(template, context, (IRegion) region, getRelevance(template, prefix)));
		}
	}
	
	@Override
	protected String getTempleteContextType() {
		// TODO Auto-generated method stub
		return null;
	}
	
}