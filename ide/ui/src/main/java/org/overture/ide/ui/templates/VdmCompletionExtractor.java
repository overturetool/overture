package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
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
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
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

public final class VdmCompletionExtractor extends VdmTemplateAssistProcessor {
	
	static VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	
	public VdmCompletionExtractor(){}
	
	    
	public void completeBasicTypes(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			final int offset, List<INode> Ast, final TemplateContext context,final ITextViewer viewer)
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
					
					
					@Override
					public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = explicitFunctionNameExtractor(node,info);
						
						functionTemplateCreator(extractedName,offset,context,proposals,info,viewer);
						
						
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
					
					@Override
					public void caseAVariableExp(AVariableExp node)
			                  throws AnalysisException{
						String name = node.toString();
						createProposal(node,name,name,name,info,proposals,offset);
					}

				});
			} catch (AnalysisException e)
			{
				VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
						+ "faild during populateNameList", e);
			}
		}
	}
	
	
	private void functionTemplateCreator(String[] extractedName, int offset,TemplateContext context,List<ICompletionProposal> proposals,VdmCompletionContext info,ITextViewer viewer) {
		
		if (context == null)
			return;
		
		if(nullOrEmptyCheck(extractedName[0])){

			ITextSelection selection = (ITextSelection) viewer
					.getSelectionProvider().getSelection();
			// adjust offset to end of normalized selection
			if (selection.getOffset() == offset)
				offset = selection.getOffset() + selection.getLength();
			String prefix = extractPrefix(viewer, offset);
			Region region = new Region(offset - prefix.length(), prefix.length());

			context.setVariable("selection", selection.getText());
			
			Template template = new Template(extractedName[0],extractedName[0],"org.overture.ide.vdmpp.ui.contextType",extractedName[1],true);

			proposals.add(createProposal(template, context, (IRegion) region,
					getRelevance(template, prefix)));
			
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
    
    private void createCallParamProposal(INode node, String displayname, String replacmentString,String additionalProposalInfo,final VdmCompletionContext info, 
    		final List<ICompletionProposal> proposals,final int offset){
    	if(replacmentString != null && !replacmentString.isEmpty())
		{	
			IContextInformation contextInfo = new ContextInformation(displayname, displayname); //$NON-NLS-1$
			
			replacmentString = info.proposalPrefix.trim() + replacmentString;
			
			int curOffset = offset + info.offset;// - info2.proposalPrefix.length();
			int length = replacmentString.length();
			int replacementLength = info.proposalPrefix.length();
			
			proposals.add(new CompletionProposal(replacmentString, curOffset, replacementLength, length, imgProvider.getImageLabel(node, 0), displayname, contextInfo, additionalProposalInfo));
		}
    }
    
    private String[] explicitFunctionNameExtractor(AExplicitFunctionDefinition node, final VdmCompletionContext info){
    	String functionName[] = new String[2];
    	
    	String[] parts = node.toString().split(":");
    	parts = parts[0].split(" ");
    	
    	if(parts[parts.length-1] != null && !parts[parts.length-1].isEmpty()){
    		functionName[0] = parts[parts.length-1];
    		functionName[1] = functionName[0] + "(";  //ReplacemestString
    		//functionName[0] = functionName[1] + ")"; //DisplayString
    	}
    	List<String> extractedNames = null;
		//if (Objects.equals(functionName[1], info.proposalPrefix)) {
			extractedNames = explicitParameterNameExtractor(node, info.proposalPrefix);
		//}	
		
		StringBuilder sb = new StringBuilder();
		sb.append(functionName[1]);
		if((extractedNames != null && !extractedNames.isEmpty())){
			
			for (int i = 0; i < extractedNames.size(); i++) {
				String str = extractedNames.get(i);
			
				if(str != extractedNames.get(0)){
					sb.append(", ");
				}
				sb.append("${" + str + "}");
			}
			
		}
		sb.append(")");
		functionName[1] = sb.toString();
		functionName[0] = functionName[1];

    	
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
    
    public void completeCallParams(final VdmCompletionContext info,
			VdmDocument document, final List<ICompletionProposal> proposals,
			final int offset, List<INode> Ast, final TemplateContext context) {
    	for (final INode element : Ast)
		{
			try
			{
				element.apply(new DepthFirstAnalysisAdaptor()
				{
					@Override
					public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = explicitFunctionNameExtractor(node,info);

//						if(nullOrEmptyCheck(extractedName[0])){
//
//				
//							int newOffset = offset;
//							
//							ITextSelection selection = (ITextSelection) viewer
//									.getSelectionProvider().getSelection();
//							// adjust offset to end of normalized selection
//							if (selection.getOffset() == newOffset)
//								newOffset = selection.getOffset() + selection.getLength();
//							
//							String prefix = extractPrefix(viewer, newOffset);
//							Region region = new Region(newOffset - prefix.length(), prefix.length());
//							TemplateContext context = createContext(viewer, region);
//							if (context == null)
//								return;
//							context.setVariable("selection", selection.getText());
//							
//							Template template = new Template(extractedName[0],extractedName[0],"org.overture.ide.vdmpp.ui.contextType",extractedName[1],true);
//
//							proposals.add(createProposal(template, context, (IRegion) region,
//									getRelevance(template, prefix)));
//							
//						}
					}

				});
			} catch (AnalysisException e)
			{
				VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
						+ "faild during populateNameList", e);
			}
		}
		
	}
    
    private List<String> explicitParameterNameExtractor(AExplicitFunctionDefinition node, String proposalPrefix) {

    	List<String> ParameterNameList = new ArrayList<String>();
    	LinkedList<List<PPattern>> strList = node.getParamPatternList();
    	List<PPattern> paramList = strList.getFirst();
    	for (PPattern str : paramList) {

    		ParameterNameList.add(str.toString());
		}

		return ParameterNameList;
	}
    
    private String implicitParameterNameExtractor(AImplicitFunctionDefinition node) {
//    	StringBuilder sb = new StringBuilder();
//    	LinkedList<APatternListTypePair> strList = node.getParamPatterns();
//    	List<PPattern> paramList = strList.getFirst();
//    	for (PPattern str : paramList) {
//			if(str != paramList.get(0)){
//				sb.append(", ");
//			}
//    		sb.append(str.toString());
//		}
//    	sb.append(")");
//		return sb.toString();
    	
    	return null;
	}


	@Override
	protected String getTempleteContextType() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
