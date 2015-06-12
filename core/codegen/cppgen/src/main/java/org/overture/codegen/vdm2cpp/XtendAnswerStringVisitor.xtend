package org.overture.codegen.vdm2cpp

import org.overture.codegen.cgast.analysis.AnswerAdaptor
import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.analysis.AnalysisException

class XtendAnswerStringVisitor extends AnswerAdaptor<String> {
	
	override createNewReturnValue(INode node) throws AnalysisException {
		return ""
	}
	
	override createNewReturnValue(Object node) throws AnalysisException {
		return ""
	}
	
}