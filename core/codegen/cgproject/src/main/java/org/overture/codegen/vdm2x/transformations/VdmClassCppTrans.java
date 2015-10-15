package org.overture.codegen.vdm2x.transformations;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class VdmClassCppTrans extends DepthFirstAnalysisAdaptor {
	
	private BaseTransformationAssistant baseAssistant;
	private TransAssistantCG trans;
	
	public VdmClassCppTrans(BaseTransformationAssistant baseAss) {
		baseAssistant = baseAss;
	}
	
}
