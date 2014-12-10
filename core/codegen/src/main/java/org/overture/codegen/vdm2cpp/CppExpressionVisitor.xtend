package org.overture.codegen.vdm2cpp

import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.SExpCGBase
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptorAnswer
import org.overture.codegen.cgast.declarations.AClassDeclCG

class CppExpressionVisitor extends XtendBaseDepthFirstCG {
	
	DepthFirstAnalysisAdaptorAnswer<String> root;
	
	new(DepthFirstAnalysisAdaptorAnswer<String> root_visitor){
		root = root_visitor;
	}
	
	override defaultINode(INode node) throws AnalysisException {
		if( node instanceof SExpCGBase )
		{
			System.out.println("unhandled expression node: " + node.getClass.toString() )
			System.out.println( (node as SExpCGBase).tag)
			System.out.println("In Class: " + node.getAncestor(AClassDeclCG).name)
			return '''/*unhandled exp «node.getClass.toString()»*/'''
		}
		else
		{
			return node.apply(root)
		}
	}
	
	
	
}