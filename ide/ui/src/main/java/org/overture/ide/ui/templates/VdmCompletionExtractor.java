package org.overture.ide.ui.templates;

import java.util.List;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AFunctionValueImport;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;

public final class VdmCompletionExtractor {
	
	static VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	
	public VdmCompletionExtractor(){}
	
	    
	public void completeBasicTypes(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			final int offset, List<INode> Ast)
	{
		for (final INode element : Ast)
		{
			try
			{
				element.apply(new DepthFirstAnalysisAdaptor()
				{
					@Override
					public void caseANatNumericBasicType(ANatNumericBasicType node)
							throws AnalysisException
					{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseANatOneNumericBasicType(ANatOneNumericBasicType node)
							throws AnalysisException
					{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseABooleanBasicType(ABooleanBasicType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseAIntNumericBasicType(AIntNumericBasicType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseARationalNumericBasicType(ARationalNumericBasicType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseARealNumericBasicType(ARealNumericBasicType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseACharacterPattern(ACharacterPattern node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseACharBasicType(ACharBasicType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseATokenBasicType(ATokenBasicType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void defaultSNumericBasicType(SNumericBasicType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseAClassInvariantStm(AClassInvariantStm node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseAClassInvariantDefinition(AClassInvariantDefinition node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseANamedInvariantType(ANamedInvariantType node){
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
				
					@Override
					public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = functionNameExtractor(node);
						
						if(extractedName[0] != null && !extractedName[0].isEmpty() && extractedName[1] != null && !extractedName[1].isEmpty()){
							createProposal(node,extractedName[0],extractedName[1],node.toString(),info,proposals,offset);
						}
					}
					
					@Override
					public void caseAImplicitFunctionDefinition(AImplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = functionNameExtractor(node);
						
						if(extractedName[0] != null && !extractedName[0].isEmpty() && extractedName[1] != null && !extractedName[1].isEmpty()){
							createProposal(node,extractedName[0],extractedName[1],node.toString(),info,proposals,offset);
						}
					}

				});
			} catch (AnalysisException e)
			{
				VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
						+ "faild during populateNameList", e);
			}
		}
	}

    public boolean findInString(String text,String word)
	{
	  return word.toLowerCase().startsWith(text.toLowerCase());
	}
    
    private void createProposal(INode node, String displayname, String replacmentString,String additionalProposalInfo,final VdmCompletionContext info, 
    		final List<ICompletionProposal> proposals,final int offset)
    {
    	if(findInString(info.proposalPrefix,replacmentString) && replacmentString != null && !replacmentString.isEmpty())
		{	
			IContextInformation contextInfo = new ContextInformation(displayname, displayname); //$NON-NLS-1$

			int curOffset = offset + info.offset;// - info2.proposalPrefix.length();
			int length = replacmentString.length();
			int replacementLength = info.proposalPrefix.length();

			proposals.add(new CompletionProposal(replacmentString, curOffset, replacementLength, length, imgProvider.getImageLabel(node, 0), displayname, contextInfo, additionalProposalInfo));
		}
    }
    
    private static String[] functionNameExtractor(INode node){
    	
    	String functionName[] = new String[2];
    	
    	String[] parts = node.toString().split(" ");
    	
    	if(parts[1] != null && !parts[1].isEmpty()){
    		functionName[0] = parts[1];
    		functionName[1] = functionName[0].replace(":","(");
    		functionName[0] = functionName[1].replace("(","()");
    	}
    	
    	return functionName;
    } 
    
}
