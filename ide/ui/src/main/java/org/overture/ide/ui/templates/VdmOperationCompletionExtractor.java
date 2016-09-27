package org.overture.ide.ui.templates;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;

public class VdmOperationCompletionExtractor {
	
	private static VdmCompletionHelper VdmHelper = new VdmCompletionHelper();
	
	public String[] explicitOperationNameExtractor(AExplicitOperationDefinition node){
		String[] functionName = new String[2];
		String[] SplitName = (node.getName().toString()).split("\\(");
    	functionName[0] = SplitName[0];
    	
    	if(functionName[0] != null && !functionName[0].isEmpty()){
    		functionName[1] = functionName[0] + "(";  //ReplacemestString
    	}
    	List<String> parameterNameList = null;

    	parameterNameList = explicitOperationParameterNameExtractor(node);

    	return VdmHelper.templatePatternGenerator(parameterNameList,functionName);
    }
	
	public String[] implicitOperationNameExtractor(AImplicitOperationDefinition node){
		String[] functionName = new String[2];
		String[] SplitName = (node.getName().toString()).split("\\(");
    	functionName[0] = SplitName[0];
    	
    	if(functionName[0] != null && !functionName[0].isEmpty()){
    		functionName[1] = functionName[0] + "(";  //ReplacemestString
    	}
    	List<String> parameterNameList = null;

    	parameterNameList = implicitOperationParameterNameExtractor(node);

    	return VdmHelper.templatePatternGenerator(parameterNameList,functionName);
    }
	
	public List<String> explicitOperationParameterNameExtractor(AExplicitOperationDefinition node) {

    	List<String> parameterNameList = new ArrayList<String>();
    	LinkedList<PPattern> strList = node.getParameterPatterns();
    	
    	for(int i = 0;i < strList.size(); i++){
    		parameterNameList.add(strList.get(i).toString());
    	}
		return parameterNameList;
	}
	
	public List<String> implicitOperationParameterNameExtractor(AImplicitOperationDefinition node) {

    	List<String> parameterNameList = new ArrayList<String>();
    	LinkedList<APatternListTypePair> strList = node.getParameterPatterns();

    	for(int i = 0;i < strList.size(); i++){
    		parameterNameList.add(strList.get(i).getPatterns().getFirst().toString());
    	}
		return parameterNameList;
	}
}