package org.overture.codegen.vdm2cpp

import org.overture.codegen.merging.MergeVisitor
import org.overture.codegen.merging.TemplateStructure
import org.overture.codegen.merging.TemplateCallable
import org.overture.codegen.cgast.INode
import java.io.StringWriter
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.SStmCGBase
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.statements.ASkipStmCG
import org.overture.codegen.cgast.statements.APlainCallStmCG
import org.overture.codegen.cgast.STypeCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.cgast.statements.ACallObjectStmCG
import org.overture.codegen.cgast.statements.AForAllStmCG
import org.overture.codegen.cgast.statements.AWhileStmCG
import org.overture.codegen.cgast.statements.ABreakStmCG
import org.overture.codegen.cgast.statements.AForLoopStmCG
import org.overture.ast.types.AVoidType
import org.overture.codegen.cgast.statements.AForIndexStmCG
import org.overture.codegen.cgast.statements.AIfStmCG
import org.overture.codegen.cgast.statements.AReturnStmCG
import org.overture.codegen.cgast.statements.ABlockStmCG
import org.overture.codegen.cgast.statements.AAssignmentStmCG
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG

class CppStmVisitor extends MergeVisitor{
	
	vdm2cppGen root_generator;
	
	new(vdm2cppGen root,TemplateStructure templateStructure, TemplateCallable[] templateCallables) {
		super(templateStructure, templateCallables)
		
		root_generator = root
		
	}
	
	def expand(INode node)
	{
		var str = new StringWriter()
		node.apply(this,str)
		return str.toString()
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
	
	def boolean getIsVoid(STypeCG cg)
	{
		return cg.class == AVoidType;
	}
	
	override defaultINode(INode node, StringWriter question) throws AnalysisException {
		
		if( node instanceof SStmCGBase )
		{
			System.out.println("unhandled type node: " + node.getClass.toString() )
			System.out.println( (node as SStmCGBase).tag)
			System.out.println("In Class: " + node.getAncestor(AClassDeclCG).name)
			question.append('''/*unhandled Statement«node.getClass.toString()»*/''')
		}
		else
		{
			node.apply(root_generator,question)
		}
	}
	
	override caseASkipStmCG(ASkipStmCG node, StringWriter question) throws AnalysisException {
		question.append("//skip")
	}
	
	override caseAPlainCallStmCG(APlainCallStmCG node, StringWriter question)
	{
		question.append('''«IF node.classType != null»«node.classType.getStaticCall»::«ENDIF»«node.name»( «FOR arg: node.args SEPARATOR ','» «arg.expand» «ENDFOR»); 
		''');
	}
	
	override caseAForLoopStmCG(AForLoopStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''
		for («node.init.expand»; «IF node.cond != null»«node.cond.expand»«ENDIF»; «IF node.inc != null»«node.inc.expand»«ENDIF» )
		{
			«node.body.expand»
		}
		'''
		)
	}
	
	override caseABreakStmCG(ABreakStmCG node, StringWriter question)
	{
		question.append('''break;''')
	}
	
	override caseAWhileStmCG(AWhileStmCG node, StringWriter question)
	{
		question.append('''while(«node.exp.expand»)
		{
			«node?.body.expand»
		}
		''')
	}
	
	override caseAForAllStmCG(AForAllStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		''' // FIXME
		for( «node.exp.type.expand» «node.pattern» : «node.exp.expand»)
		{
			«node.body.expand»
		}
		'''
		)
	}
	
	override caseACallObjectStmCG(ACallObjectStmCG node, StringWriter question) throws AnalysisException {
		question.append('''«IF node.type.isVoid»return«ENDIF»«node.designator.expand»->«node.fieldName»(«FOR arg : node.args SEPARATOR ','»«arg.expand»«ENDFOR»); //dd
		''')
	}
	
	override caseAForIndexStmCG(AForIndexStmCG node, StringWriter question) throws AnalysisException {
		question.append('''
		for(«node.^var» = «node.from.expand» ; «node.^var» <= «node.to.expand» ; «node.^var»++ )
		{
			«node.body?.expand»
		}
		''')
	}
	
	override caseABlockStmCG(ABlockStmCG node, StringWriter question) throws AnalysisException {
		
		question.append('''
		«FOR local_def : node.localDefs»
		«local_def.expand»
		«ENDFOR»
		«FOR stm : node.statements»«stm.expand»«ENDFOR»''')
	}

	override caseAReturnStmCG(AReturnStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''
		return «node.exp.expand»;
		'''
		)
	}
	
	override caseALocalAssignmentStmCG(ALocalAssignmentStmCG node, StringWriter question)
	{
		question.append('''«node.target.expand» = «node.exp.expand»;
		''')
	}
	
	override caseAIfStmCG(AIfStmCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''
		if( «node.ifExp.expand» )
		{
			«node.thenStm.expand»
		}
		«FOR elif : node.elseIf»else if( «elif.elseIf.expand» )
		{
			«elif.thenStm.expand»
		}
		«ENDFOR»
		«IF node.elseStm != null»
		else
		{
			«node.elseStm.expand»
		}
		«ENDIF»
		'''
		)
	}
	
	override caseAAssignmentStmCG(AAssignmentStmCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.target.expand» = «node.exp.expand»; 
		''')
	}
	
}