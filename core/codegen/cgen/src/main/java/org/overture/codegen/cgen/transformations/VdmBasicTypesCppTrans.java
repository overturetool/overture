package org.overture.codegen.cgen.transformations;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.ANatBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class VdmBasicTypesCppTrans extends DepthFirstAnalysisAdaptor {
	
	private BaseTransformationAssistant baseAssistant;
	private TransAssistantCG trans;
	
	public VdmBasicTypesCppTrans(BaseTransformationAssistant baseAss) {
		baseAssistant = baseAss;
	}
	
	@Override
	public void caseANatNumericBasicTypeCG(ANatNumericBasicTypeCG node)
			throws AnalysisException {
		AExternalTypeCG n = new AExternalTypeCG();
		n.setName("int");
		
		
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseARealNumericBasicTypeCG(ARealNumericBasicTypeCG node)
			throws AnalysisException {
		AExternalTypeCG n = new AExternalTypeCG();
		n.setName("double");
		
		
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseABoolBasicTypeCG(ABoolBasicTypeCG node)
			throws AnalysisException {
		AExternalTypeCG n = new AExternalTypeCG();
		n.setName("bool");
		
		baseAssistant.replaceNodeWith(node, n);
	}
	
	
}
