package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
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
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.internal.viewsupport.VdmElementImageProvider;

public final class VdmCompletionExtractor extends VdmTemplateAssistProcessor {
	
	static VdmElementImageProvider imgProvider = new VdmElementImageProvider();
	
	private ArrayList<String> basicTypes = new ArrayList<String>() {{
	    add("nat");
	    add("nat1");
	    add("bool");
	    add("int");
	    add("real");
	    add("char");
	}};
	
	private ArrayList<String> dynamicTemplateProposals = new ArrayList<String>();
	private ArrayList<String> dynamicProposals = new ArrayList<String>();
	
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
					public void caseAInstanceVariableDefinition(AInstanceVariableDefinition node)
                            throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,"Instance Variable",info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}

					@Override
					public void caseALetBeStBindingTraceDefinition(ALetBeStBindingTraceDefinition node)
                            throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,name,info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}
					
					@Override
					public void caseALetBeStExp(ALetBeStExp node)
		                     throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,name,info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}
					
					@Override
					public void caseALetBeStStm(ALetBeStStm node)
		                     throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,name,info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}
					
					@Override
					public void caseALetDefBindingTraceDefinition(ALetDefBindingTraceDefinition node)
                            throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,name,info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}
					
					@Override
					public void caseALetDefExp(ALetDefExp node)
		                    throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,name,info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}
					
					@Override
					public void caseALetStm(ALetStm node)
			                 throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,name,info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}
					
					@Override
					public void caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = explicitFunctionNameExtractor(node,info);
						if(nullOrEmptyCheck(extractedName[1]) && !checkForDuplicates(extractedName[1],dynamicTemplateProposals)){
							functionTemplateCreator(extractedName,offset,context,proposals,info,viewer,node.getLocation().getEndOffset());
							dynamicTemplateProposals.add(extractedName[1]);
						}
					}
					
					@Override
					public void caseAImplicitFunctionDefinition(AImplicitFunctionDefinition node)
                            throws AnalysisException{
						String extractedName[] = implicitFunctionNameExtractor(node);

						if(nullOrEmptyCheck(extractedName[0]) && !checkForDuplicates(extractedName[1],dynamicProposals)){
							createProposal(node,extractedName[0],extractedName[1],node.toString(),info,proposals,offset);
							dynamicProposals.add(extractedName[1]);
						}
					}
					
					@Override
					public void caseAExplicitOperationDefinition(AExplicitOperationDefinition node)
                            throws AnalysisException{
						String extractedName[] = explicitOperationNameExtractor(node);
						
						if(nullOrEmptyCheck(extractedName[0]) && !checkForDuplicates(extractedName[1],dynamicProposals)){
							createProposal(node, extractedName[0], extractedName[1], node.toString(), info, proposals, offset);
							dynamicProposals.add(extractedName[1]);
						}
					}
					
					@Override
					public void caseAImplicitOperationDefinition(AImplicitOperationDefinition node)
                            throws AnalysisException{
						String extractedName[] = implicitOperationNameExtractor(node);
						
						if(nullOrEmptyCheck(extractedName[0]) && !checkForDuplicates(extractedName[1],dynamicProposals)){
							createProposal(node, extractedName[0], extractedName[1], node.toString(), info, proposals, offset);
							dynamicProposals.add(extractedName[1]);
						}
					}
					
					@Override
					public void caseAVariableExp(AVariableExp node)
			                  throws AnalysisException{
						String name = node.toString();
						if(!checkForDuplicates(name,dynamicTemplateProposals)){
							createProposal(node,name,name,"Variable Exp",info,proposals,offset);
							dynamicTemplateProposals.add(name);
						}
					}

				});
			} catch (AnalysisException e)
			{
				VdmUIPlugin.log("Completion error in " + getClass().getSimpleName()
						+ "faild during populateNameList", e);
			}
		}
		basicTypeProposalFunction(info, proposals, offset);
		dynamicTemplateProposals.clear();
		dynamicProposals.clear();
	}
	
	private void basicTypeProposalFunction(final VdmCompletionContext info, final List<ICompletionProposal> proposals,
			final int offset){
		
		Iterator<String> iter = basicTypes.iterator();
		while(iter.hasNext()){
			String item = iter.next();
			createProposal(null,item,item,"Basic Type",info,proposals,offset);			
		}
	}
	
	private void functionTemplateCreator(String[] extractedName, int offset,TemplateContext context,List<ICompletionProposal> proposals,VdmCompletionContext info,ITextViewer viewer,int nodeOffsetPosition) {
		
		if (context == null)
			return;
		
		if(nullOrEmptyCheck(extractedName[0]) && findInString(info.proposalPrefix,extractedName[0])){

			ITextSelection selection = (ITextSelection) viewer
					.getSelectionProvider().getSelection();
			// get caret pos = selection.getOffset()
				
			if (selection.getOffset() == offset){
				offset = selection.getOffset() + selection.getLength();
			}
			String prefix = extractPrefix(viewer, offset);
			Region region = new Region(offset - prefix.length(), prefix.length());

			context.setVariable("selection", selection.getText());
			
			Template template = new Template(extractedName[0],"Explicit Function","org.overture.ide.vdmpp.ui.contextType",extractedName[1],true);
			
			proposals.add(createProposal(template, context, (IRegion) region, getRelevance(template, prefix)));
		}
	}
	
	private boolean checkForDuplicates(String value, ArrayList<String> container){
		
		Iterator<String> iter = container.iterator();

		while(iter.hasNext()){
			String item = iter.next();
			if(Objects.equals(value, item)){
				return true;
			}
		}
		return false;
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
    	if(nullOrEmptyCheck(replacmentString) && findInString(info.proposalPrefix,replacmentString))
		{	
			IContextInformation contextInfo = new ContextInformation(displayname, displayname); //$NON-NLS-1$
			
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
    	}
    	List<String> extractedNames = null;

		extractedNames = explicitParameterNameExtractor(node, info.proposalPrefix);
		
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
    
    private List<String> explicitParameterNameExtractor(AExplicitFunctionDefinition node, String proposalPrefix) {

    	List<String> ParameterNameList = new ArrayList<String>();
    	LinkedList<List<PPattern>> strList = node.getParamPatternList();
    	List<PPattern> paramList = strList.getFirst();
    	
    	for (PPattern str : paramList) {
    		ParameterNameList.add(str.toString());
		}

		return ParameterNameList;
	}
    
	@Override
	protected String getTempleteContextType() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
