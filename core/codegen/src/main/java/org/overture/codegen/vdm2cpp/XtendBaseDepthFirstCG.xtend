package org.overture.codegen.vdm2cpp

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptorAnswer
import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.analysis.AnalysisException

class XtendBaseDepthFirstCG extends DepthFirstAnalysisAdaptorAnswer<String> {
	
	override createNewReturnValue(INode node) throws AnalysisException {
		return ""
	}
	
	override createNewReturnValue(Object node) throws AnalysisException {
		return ""
	}
	
	override mergeReturns(String original, String new_) {
		return original + new_;
	}
	
	def expand(INode node)
	{
		return node.apply(this);
	}
}