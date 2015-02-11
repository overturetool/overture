package org.overture.codegen.vdm2cpp.vdmtools

import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.SDeclCG
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.declarations.AFuncDeclCG
import org.overture.codegen.cgast.declarations.AMethodDeclCG
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG
import org.overture.codegen.cgast.declarations.AVarDeclCG
import org.overture.codegen.cgast.statements.AForLoopStmCG
import org.overture.codegen.vdm2cpp.XtendAnswerStringVisitor
import org.overture.codegen.cgast.expressions.AUndefinedExpCG

class CppDeclarationsVisitor extends XtendAnswerStringVisitor {
	
	XtendAnswerStringVisitor root;
	
	new(XtendAnswerStringVisitor root_visitor) {
		root = root_visitor;
	}
	
	def expand(INode node)
	{
		return node.apply(root);
	}
	
	
	override defaultINode(INode node) throws AnalysisException {
		
		if( node instanceof SDeclCG )
		{
			
			return '''/*unhandled SDECL type«node.class.toString»*/'''
		}
		else
		{
			return node.apply(root)
		}
	}
	
	
	
	override caseAMethodDeclCG(AMethodDeclCG node)
	{
		if(node.isConstructor)
		{
			'''
			«node.name»(«FOR p : node.formalParams SEPARATOR ','» «p.type.expand» «p.pattern.expand» «ENDFOR» )
			{
				«node.body?.expand»
			}
			'''			
		}
		else
		{
			'''
			«IF node.static != null && node?.static == true»static «ELSE»virtual «ENDIF»«node.methodType.expand» «node.name»(«FOR p : node.formalParams SEPARATOR ','» «p.type.expand» «p.pattern.expand» «ENDFOR» )
			{
				«node.body?.expand»
			}
			'''
		}
	}
	
	override caseAFuncDeclCG(AFuncDeclCG node)'''
	static «node.name»(«FOR p : node.formalParams SEPARATOR ','»«p.type.expand»«p.pattern.expand»«ENDFOR»);
	'''
	
	override caseANamedTypeDeclCG(ANamedTypeDeclCG node)
	'''typedef «node.type.expand» «node.name»'''
	
	
	override caseAVarDeclCG(AVarDeclCG node)
	{
		//TODO: HACK declarations in for loops is without semicolon and auto type;
		if(node.parent instanceof AForLoopStmCG)
		{
			'''«node.type.expand» «node.pattern.expand» = «node.exp.expand»'''
		}
		else
		{
			if(node.exp instanceof AUndefinedExpCG)
			{
				'''«node.type.expand» «node.pattern.expand»;'''
			}
			else
			{
				'''«node.type.expand» «node.pattern.expand» = «node.exp.expand»;'''
			}
		}
	}
	
}