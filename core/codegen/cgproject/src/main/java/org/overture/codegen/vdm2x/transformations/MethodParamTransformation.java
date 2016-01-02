package org.overture.codegen.vdm2x.transformations;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.AIntNumericBasicType;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class MethodParamTransformation extends DepthFirstAnalysisAdaptor{
	
	private TransAssistantCG transformationAssistant;
	
	public MethodParamTransformation(TransAssistantCG transformationAssistant){
		this.transformationAssistant = transformationAssistant;
	}

	//@SuppressWarnings("unchecked")
	
	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		LinkedList<AFormalParamLocalParamCG> formalPar = node.getFormalParams();
		
		LinkedList<AFormalParamLocalParamCG> f = new LinkedList<>();
		
		System.out.println("Method name is " + node.getName());
		
		AFormalParamLocalParamCG cl = new AFormalParamLocalParamCG();
		AIdentifierPatternCG id = new AIdentifierPatternCG();
		
		AIntNumericBasicTypeCG ty = new AIntNumericBasicTypeCG();
		
		
		cl.setTag("this");
		id.setName("this"); // This one gets printed
		cl.setPattern(id);
		cl.setType(ty);
		
		f.add(cl);
		f.addAll((List<? extends AFormalParamLocalParamCG>) formalPar.clone());
		node.setFormalParams(f);
		
	}
	
}
