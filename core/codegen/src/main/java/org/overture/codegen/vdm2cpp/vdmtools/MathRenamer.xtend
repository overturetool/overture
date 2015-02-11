package org.overture.codegen.vdm2cpp.vdmtools

import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG
import org.overture.codegen.cgast.types.AClassTypeCG

class MathRenamer extends DepthFirstAnalysisAdaptor {
	
	override caseAClassTypeCG(AClassTypeCG node) throws AnalysisException {
		if(node.name.equals("MATH"))
		{
			node.name = "vdm_MATH"		
		}
		
		
		super.caseAClassTypeCG(node);
	}
	
	override caseAExplicitVarExpCG(AExplicitVarExpCG node) throws AnalysisException {
		if(node.classType != null)
		{
			if(node.classType instanceof AClassTypeCG)
			{
				var cls = node.classType as AClassTypeCG
				if(cls.name.equals("MATH")){
					node.name = "vdm_" + node.name 
					
				}
			}
		}
		super.caseAExplicitVarExpCG(node)
	}
	
	
	

	
}