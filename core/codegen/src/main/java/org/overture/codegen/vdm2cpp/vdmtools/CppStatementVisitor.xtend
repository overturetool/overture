package org.overture.codegen.vdm2cpp.vdmtools

import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.SStmCG
import org.overture.codegen.cgast.statements.ABlockStmCG
import org.overture.codegen.cgast.statements.AAssignmentStmCG
import org.overture.codegen.cgast.statements.AReturnStmCG
import org.overture.codegen.cgast.statements.APlainCallStmCG
import org.overture.codegen.cgast.statements.ASkipStmCG
import org.overture.codegen.cgast.statements.AForLoopStmCG
import org.overture.codegen.cgast.statements.AIfStmCG
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG
import org.overture.codegen.cgast.statements.ACallObjectStmCG
import org.overture.codegen.cgast.statements.AWhileStmCG
import org.overture.codegen.cgast.statements.ABreakStmCG
import org.overture.codegen.cgast.statements.AForIndexStmCG
import org.overture.codegen.vdm2cpp.XtendAnswerStringVisitor
import org.overture.codegen.cgast.STypeCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG
import org.overture.codegen.cgast.types.AVoidTypeCG

class CppStatementVisitor extends XtendAnswerStringVisitor {
	
	XtendAnswerStringVisitor root;
	
	new(XtendAnswerStringVisitor root_visitor) {
		root = root_visitor
	}
	
	def expand(INode node)
	{
		return node.apply(root)
	}
	def String getGetStaticCall(STypeCG cg)
	{
		if(cg instanceof AClassTypeCG)
		{
			 return (cg as AClassTypeCG).name
		}
		else
		{
			return "udef"
		}
	}
	
	def caseToType(STypeCG type)
	{
		if(type instanceof AClassTypeCG)
		{
			return '''ObjGet_«type.name»'''
		}
		else if(type instanceof ARealBasicTypeWrappersTypeCG || type instanceof ARealNumericBasicTypeCG)
		{
			return '''static_cast<«type.expand»>'''
		}
		else
		{
			return '''static_cast<«type.expand»>'''
		}
		
	}
	
	
	override defaultINode(INode node) throws AnalysisException {
		if(node instanceof SStmCG){
			return '''/*Not generating statement «node.class.toString»*/'''
		}
		else
		{
			return node.apply(root);
		}
	}
	
	override caseABlockStmCG(ABlockStmCG node)'''
	«FOR v : node.localDefs»
	«v.expand»
	«ENDFOR»	
	«FOR stm : node.statements»
	«stm.expand»
	«ENDFOR»
	'''
	
	override caseAForLoopStmCG(AForLoopStmCG node)'''
	for(«node.init?.expand»; «node.cond?.expand»; «node.inc?.expand»)
	{
		«node.body?.expand»
	}
	'''
	
	override caseARaiseErrorStmCG(ARaiseErrorStmCG node)
	'''vdm::Runtime2("«node.error.expand»",__FILE__,__LINE__);'''
	
	override caseAForIndexStmCG(AForIndexStmCG node)
	'''
	for(«node.from.type.expand» «node.^var» = «node.from.expand» ; «node.^var» <= «node.to.expand» ; «node.^var».Incr() )
	{
		«node.body?.expand»
	}
	'''
	
	
	override caseAWhileStmCG(AWhileStmCG node)
	'''
	while(«node.exp.expand»)
	{
		«node.body?.expand»
	}
	'''
	
	override caseAIfStmCG(AIfStmCG node)
	'''
	if(«node.ifExp.expand»)
	{
		«node.thenStm.expand»
	}
	«FOR elif: node.elseIf»
	else if(«elif.elseIf.expand»)
	{
		«elif.thenStm.expand»
	}
	«ENDFOR»
	«IF node.elseStm !=null»
	else
	{
		«node.elseStm.expand»
	}
	«ENDIF»
	'''
	
	def isVoidType(STypeCG type)
	{
		if(type instanceof AVoidTypeCG)
		{
			return true;
		}
		return false;
	}
	
	override caseACallObjectExpStmCG(ACallObjectExpStmCG node)
	'''«IF !node.type.isVoidType » return «ENDIF»«node.obj.type.caseToType»(«node.obj.expand»)->«node.fieldName»(«FOR a: node.args SEPARATOR ','» «a.expand»«ENDFOR»);'''
	
	override caseACallObjectStmCG(ACallObjectStmCG node)
	'''«node.designator»(«node.designator.expand»)->«node.fieldName»(«FOR a: node.args SEPARATOR ','» «a.expand»«ENDFOR»);'''
	
	override caseAAssignmentStmCG(AAssignmentStmCG node)
	'''«node.target.expand» = «node.exp.expand»;'''
	
	override caseAReturnStmCG(AReturnStmCG node)'''
	return «node.exp.expand»;
	'''
	
	
	override caseAPlainCallStmCG(APlainCallStmCG node)
	'''«IF node.classType != null»«node.classType.getStaticCall»::«ENDIF»«node.name»/*«node.type»*/(«FOR a : node.args SEPARATOR ','» «a.expand»«ENDFOR»);'''
	
	
	override caseASkipStmCG(ASkipStmCG node)'''
	/*skip*/
	'''
	
	override caseABreakStmCG(ABreakStmCG node)
	'''break;'''
	
	override caseALocalAssignmentStmCG(ALocalAssignmentStmCG node)
	{
		if(node.exp == null)
		{
			'''«(node.target.type as AClassTypeCG).name» «node.target.expand»;'''
		}
		else
		{
			'''«node.target.expand» = «node.exp.expand»;'''
		}
	}
	
}