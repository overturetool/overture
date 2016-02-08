package org.overture.ide.ui.templates;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AParameterType;
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
					public void setVisitedNodes(Set<INode> value){
						 
						 for (int i = 0; i < value.size(); i++) {
							System.out.println(i+": " + value);
						}
						 super.setVisitedNodes(value);
					}
					
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
					public void caseAInstanceVariableDefinition(AInstanceVariableDefinition node)
                            throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
				///
					@Override
					public void caseALetBeStBindingTraceDefinition(ALetBeStBindingTraceDefinition node)
                            throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseALetBeStExp(ALetBeStExp node)
		                     throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseALetBeStStm(ALetBeStStm node)
		                     throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseALetDefBindingTraceDefinition(ALetDefBindingTraceDefinition node)
                            throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseALetDefExp(ALetDefExp node)
		                    throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
					@Override
					public void caseALetStm(ALetStm node)
			                 throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}
					
				///	
					@Override
					public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = explicitFunctionNameExtractor(node);

						if(nullOrEmptyCheck(extractedName[0])){
							createProposal(node,extractedName[0],extractedName[1],node.toString(),info,proposals,offset);
						}
					}
					
					@Override
					public void caseAImplicitFunctionDefinition(AImplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = implicitFunctionNameExtractor(node);

						if(nullOrEmptyCheck(extractedName[0])){
							createProposal(node,extractedName[0],extractedName[1],node.toString(),info,proposals,offset);
						}
					}
					
					@Override
					public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node)
                            throws AnalysisException{
						String extractedName[] = explicitOperationNameExtractor(node);
						
						if(nullOrEmptyCheck(extractedName[0])){
							createProposal(node, extractedName[0], extractedName[1], node.toString(), info, proposals, offset);
						}
					}
					
					@Override
					public void caseAImplicitOperationDefinition(AImplicitOperationDefinition node)
                            throws AnalysisException{
						String extractedName[] = implicitOperationNameExtractor(node);
						
						if(nullOrEmptyCheck(extractedName[0])){
							createProposal(node, extractedName[0], extractedName[1], node.toString(), info, proposals, offset);
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
	
	private boolean nullOrEmptyCheck(String str){
		return str != null && !str.isEmpty();
	}

    public boolean findInString(String text,String word)
	{
    	if(text == ""){
    		return true;
    	}
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
    
    private static String[] explicitFunctionNameExtractor(INode node){
    	String functionName[] = new String[2];
    	
    	String[] parts = node.toString().split(" ");
    	
    	if(parts[1] != null && !parts[1].isEmpty()){
    		functionName[0] = parts[1];
    		functionName[1] = functionName[0].replace(":","(");  //ReplacemestString
    		functionName[0] = functionName[1].replace("(","()"); //DisplayString
    	}
    	
    	return functionName;
    }
    
    private static String[] implicitFunctionNameExtractor(INode node){
    	String functionName[] = new String[2];
    	
    	String[] parts1 = node.toString().split(" ");
    	String[] parts2 = parts1[2].split("\\(");
    	
    	if(parts2[0] != null && !parts2[0].isEmpty()){
    		functionName[0] = parts2[0];
    		functionName[1] = new StringBuilder(functionName[0]).append("(").toString(); //ReplacemestString
    		functionName[0] = new StringBuilder(functionName[1]).append(")").toString(); //DisplayString
    	}
    	
    	return functionName;
    }
    
    private static String[] explicitOperationNameExtractor(INode node){
		String functionName[] = new String[2];
    	
    	String[] parts = node.toString().split(" ");
    	
    	if(parts[0] != null && !parts[0].isEmpty()){
    		functionName[0] = parts[0];
    		functionName[1] = new StringBuilder(functionName[0]).append("(").toString(); //ReplacemestString
    		functionName[0] = new StringBuilder(functionName[1]).append(")").toString(); //DisplayString
    	}
    	
    	return functionName;
    }
    
    private static String[] implicitOperationNameExtractor(INode node){
		String functionName[] = new String[2];
    	
    	String[] parts = node.toString().split("\\(");
    	
    	if(parts[0] != null && !parts[0].isEmpty()){
    		functionName[0] = parts[0];
    		functionName[1] = new StringBuilder(functionName[0]).append("(").toString(); //ReplacemestString
    		functionName[0] = new StringBuilder(functionName[1]).append(")").toString(); //DisplayString
    	}
    	
    	return functionName;
    }
    
}
