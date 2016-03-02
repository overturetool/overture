package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;

public class VdmFunctionCompletionExtractor extends VdmTemplateAssistProcessor{
	
	private static VdmCompletionHelper VdmHelper = new VdmCompletionHelper();
	
	public static String[] implicitFunctionNameExtractor(INode node){
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
    
	public static String[] explicitOperationNameExtractor(INode node){
		String functionName[] = new String[2];
    	String[] parts = node.toString().split(" ");
    	
    	if(parts[0] != null && !parts[0].isEmpty()){
    		functionName[0] = parts[0];
    		functionName[1] = new StringBuilder(functionName[0]).append("(").toString(); //ReplacemestString
    		functionName[0] = new StringBuilder(functionName[1]).append(")").toString(); //DisplayString
    	}
    	
    	return functionName;
    }
    
	public String[] implicitOperationNameExtractor(INode node){
		String functionName[] = new String[2];
    	
    	String[] parts = node.toString().split("\\(");
    	
    	if(parts[0] != null && !parts[0].isEmpty()){
    		functionName[0] = parts[0];
    		functionName[1] = new StringBuilder(functionName[0]).append("(").toString(); //ReplacemestString
    		functionName[0] = new StringBuilder(functionName[1]).append(")").toString(); //DisplayString
    	}
    	
    	return functionName;
    }
    
	public List<String> explicitParameterNameExtractor(AExplicitFunctionDefinition node, String proposalPrefix) {

    	List<String> ParameterNameList = new ArrayList<String>();
    	LinkedList<List<PPattern>> strList = node.getParamPatternList();
    	List<PPattern> paramList = strList.getFirst();
    	
    	for (PPattern str : paramList) {
    		ParameterNameList.add(str.toString());
		}

		return ParameterNameList;
	}
	
    
	public String[] explicitFunctionNameExtractor(AExplicitFunctionDefinition node, final VdmCompletionContext info){
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
    

	public void functionTemplateCreator(String[] extractedName, int offset,TemplateContext context,List<ICompletionProposal> proposals,VdmCompletionContext info,ITextViewer viewer,int nodeOffsetPosition) {
		
		if (context == null)
			return;
		
		if(VdmHelper.nullOrEmptyCheck(extractedName[0]) && VdmHelper.findInString(info.proposalPrefix,extractedName[0])){

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

	@Override
	protected String getTempleteContextType() {
		// TODO Auto-generated method stub
		return null;
	}
    
}
