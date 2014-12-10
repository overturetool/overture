package org.overture.codegen.vdm2cpp.ExpressionVisitors

import org.overture.codegen.merging.MergeVisitor
import org.overture.codegen.merging.TemplateStructure
import org.overture.codegen.merging.TemplateCallable
import org.overture.codegen.cgast.INode
import java.io.StringWriter
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.expressions.SSeqExpCG
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG

class SeqExpVisitor extends MergeVisitor {
	
	MergeVisitor root_visitor;
	new(MergeVisitor root,TemplateStructure templateStructure, TemplateCallable[] templateCallables) {
		super(templateStructure, templateCallables)
		
		root_visitor = root;
		}
	
		def expand(INode node)
	{
		var str = new StringWriter()
		node.apply(this,str)
		return str.toString()
	}
	
	override defaultINode(INode node, StringWriter question) throws AnalysisException {
		
		if( node instanceof SSeqExpCG )
		{
			System.out.println("unhandled seq expression node: " + node.getClass.toString() )
			System.out.println( (node as SSeqExpCG).tag)
			System.out.println("In Class: " + node.getAncestor(AClassDeclCG).name)
			question.append('''/*unhandled seq exp «node.getClass.toString()»*/''')
		}
		else
		{
			node.apply(root_visitor,question)
		}
	}
	
	

	
	
}