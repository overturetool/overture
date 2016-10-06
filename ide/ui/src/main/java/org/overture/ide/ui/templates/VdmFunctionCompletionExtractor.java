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
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;

public class VdmFunctionCompletionExtractor{
	
	private static VdmCompletionHelper VdmHelper = new VdmCompletionHelper();
	
	public String[] implicitFunctionNameExtractor(AImplicitFunctionDefinition node){

    	String functionName[] = new String[2];
    	String[] SplitName = (node.getName().toString()).split("\\(");
    	functionName[0] = SplitName[0];
    	
    	if(functionName[0] != null && !functionName[0].isEmpty()){
    		functionName[1] = functionName[0] + "(";  //ReplacemestString
    	}
    	List<String> parameterNameList = null;

    	parameterNameList = implicitFunctionParameterNameExtractor(node);
		
    	return VdmHelper.templatePatternGenerator(parameterNameList,functionName);
    	
    }
	
	public String[] explicitFunctionNameExtractor(AExplicitFunctionDefinition node){
    	String functionName[] = new String[2];
    	String[] SplitName = (node.getName().toString()).split("\\(");
    	functionName[0] = SplitName[0];
    	
    	if(functionName[0] != null && !functionName[0].isEmpty()){
    		functionName[1] = functionName[0] + "(";  //ReplacemestString
    	}
    	List<String> parameterNameList = null;

    	parameterNameList = explicitFunctionParameterNameExtractor(node);
		
    	return VdmHelper.templatePatternGenerator(parameterNameList,functionName);
    }
	
	public List<String> explicitFunctionParameterNameExtractor(AExplicitFunctionDefinition node) {

    	List<String> parameterNameList = new ArrayList<String>();
    	LinkedList<List<PPattern>> strList = node.getParamPatternList();
    	List<PPattern> paramList = strList.getFirst();
    	
    	for (PPattern str : paramList) {
    		parameterNameList.add(str.toString());
		}

		return parameterNameList;
	}
	
	public List<String> implicitFunctionParameterNameExtractor(AImplicitFunctionDefinition node) {

    	List<String> parameterNameList = new ArrayList<String>();
    	LinkedList<APatternListTypePair> strList = node.getParamPatterns();

    	for(int i = 0;i < strList.size(); i++){
    		parameterNameList.add(strList.get(i).getPatterns().getFirst().toString());
    	}
		return parameterNameList;
	}
}