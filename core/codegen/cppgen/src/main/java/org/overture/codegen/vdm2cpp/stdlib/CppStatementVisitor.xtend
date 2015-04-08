package org.overture.codegen.vdm2cpp.stdlib

import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.SStmCG
import org.overture.codegen.cgast.STypeCG
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.statements.AAssignmentStmCG
import org.overture.codegen.cgast.statements.ABlockStmCG
import org.overture.codegen.cgast.statements.ABreakStmCG
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG
import org.overture.codegen.cgast.statements.ACallObjectStmCG
import org.overture.codegen.cgast.statements.AForAllStmCG
import org.overture.codegen.cgast.statements.AForIndexStmCG
import org.overture.codegen.cgast.statements.AForLoopStmCG
import org.overture.codegen.cgast.statements.AIfStmCG
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG
import org.overture.codegen.cgast.statements.APlainCallStmCG
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG
import org.overture.codegen.cgast.statements.AReturnStmCG
import org.overture.codegen.cgast.statements.ASkipStmCG
import org.overture.codegen.cgast.statements.AStackDeclStmCG
import org.overture.codegen.cgast.statements.AWhileStmCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.cgast.types.AVoidTypeCG
import org.overture.codegen.vdm2cpp.XtendAnswerStringVisitor
import org.overture.codegen.cgast.statements.AAssignToExpStmCG

class CppStatementVisitor extends XtendAnswerStringVisitor {
	
	XtendAnswerStringVisitor root;
	int uid = 0;
	
	new(XtendAnswerStringVisitor root_visitor) {
		root = root_visitor
		
	}
	
	def uniqueName(String name)
	{
		uid += 1;
		return '''«name»_«uid»'''
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
	
	override caseAForAllStmCG(AForAllStmCG node)
	{
		
	val set_name = "forall_stm_set".uniqueName
	'''
	«node.exp.type.expand» «set_name» =  «node.exp.expand»;
	for(«node.exp.type.expand»::iterator it =«set_name».begin(); it!=«set_name».end();++it)
	{
		«node.exp.type.expand»::value_type «node.pattern.expand» = *it;
		
		«node.body.expand»
	}
	'''
	}
	override caseARaiseErrorStmCG(ARaiseErrorStmCG node)
	'''vdm::Runtime2("«node.error.expand»",__FILE__,__LINE__);'''
	
	override caseAForIndexStmCG(AForIndexStmCG node)
	'''
	for(«node.from.type.expand» «node.^var» = «node.from.expand» ; «node.^var» <= «node.to.expand» ; ++«node.^var» )
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
		return type instanceof AVoidTypeCG;
	}
	
	override caseACallObjectExpStmCG(ACallObjectExpStmCG node)
	'''«IF !node.type.isVoidType » return «ENDIF»«node.obj.expand»->«node.fieldName»(«FOR a: node.args SEPARATOR ','» «a.expand»«ENDFOR»);'''
	
	override caseACallObjectStmCG(ACallObjectStmCG node)
	'''«node.designator»(«node.designator.expand»)->«node.fieldName»(«FOR a: node.args SEPARATOR ','» «a.expand»«ENDFOR»);'''
	
	override caseAAssignmentStmCG(AAssignmentStmCG node)
	'''«node.target.expand» = «node.exp.expand»;'''
	
	override caseAReturnStmCG(AReturnStmCG node)'''
	return «node.exp.expand»;
	'''
	
	
	override caseAPlainCallStmCG(APlainCallStmCG node)
	'''«IF node.classType != null»«node.classType.getStaticCall»::«ENDIF»«node.name»(«FOR a : node.args SEPARATOR ','» «a.expand»«ENDFOR»);'''
	
	
	override caseASkipStmCG(ASkipStmCG node)'''
	/*skip*/
	'''
	
	override caseABreakStmCG(ABreakStmCG node)
	'''break;'''
	
	override caseALocalAssignmentStmCG(ALocalAssignmentStmCG node)
	{
		if(node.exp == null && node.target.type instanceof AClassTypeCG) // TODO: Hack for constructing a class on the stack
		{
			'''«(node.target.type as AClassTypeCG).name» «node.target.expand»;'''
		}
		else
		{
			'''«node.target.expand» = «node.exp.expand»;'''
		}
	}
	
	override caseAAssignToExpStmCG(AAssignToExpStmCG node)
	'''«node.target.expand» = «node.exp.expand»;'''
	
	override caseAStackDeclStmCG(AStackDeclStmCG node) throws AnalysisException
	'''«node.type.expand» «node.name»(«FOR a:node.args»«a.expand»«ENDFOR»);'''
	
}