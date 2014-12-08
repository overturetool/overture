package org.overture.codegen.vdm2cpp

import org.overture.codegen.merging.TemplateStructure
import org.overture.codegen.merging.TemplateCallable
import org.overture.codegen.merging.MergeVisitor
import java.io.StringWriter
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.types.ARecordTypeCG
import org.overture.codegen.cgast.types.ASetSetTypeCG
import org.overture.codegen.cgast.types.AMapMapTypeCG
import org.overture.codegen.cgast.types.AUnionTypeCG
import org.overture.codegen.cgast.types.ACharBasicTypeCG
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG
import org.overture.codegen.cgast.types.ABoolBasicTypeCG
import org.overture.codegen.cgast.types.AStringTypeCG
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG
import org.overture.codegen.cgast.types.ASeqSeqTypeCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.cgast.types.AUnknownTypeCG
import org.overture.codegen.cgast.STypeCGBase
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.types.ATupleTypeCG
import org.overture.codegen.cgast.types.AMethodTypeCG
import org.overture.codegen.cgast.types.AVoidTypeCG

class CppTypeVisitor extends MergeVisitor{
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
	
	override defaultINode(INode node, StringWriter question) throws AnalysisException {
		
		if( node instanceof STypeCGBase )
		{
			System.out.println("unhandled type node: " + node.getClass.toString() )
			System.out.println( (node as STypeCGBase).tag)
			System.out.println("In Class: " + node.getAncestor(AClassDeclCG).name)
			question.append('''/*unhandled type*/''')
		}
		else
		{
			node.apply(root_generator,question)
		}
	}
	
	override caseAVoidTypeCG(AVoidTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''void''')
	}
	
	override caseAMethodTypeCG(AMethodTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.result.expand»''')
	}
	
	override caseATupleTypeCG(ATupleTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''std::tuple<«FOR type: node.types SEPARATOR ","»«type.expand» «ENDFOR»>''')
	}
	
	override caseANatNumericBasicTypeCG(ANatNumericBasicTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''int''')
	}
	
	override caseAUnknownTypeCG(AUnknownTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''boost::any''');
	}
	
	override caseASeqSeqTypeCG(ASeqSeqTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''vdm::sequence''')
	}
	
	override caseARecordTypeCG(ARecordTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.name.expand»''')
	}
	
	override caseASetSetTypeCG(ASetSetTypeCG node, StringWriter question) throws AnalysisException 
	{
			question.append(''' vdm::set''')
	}

	override caseAMapMapTypeCG(AMapMapTypeCG node, StringWriter question) throws AnalysisException {
		question.append(
		'''vdm::map<«node.from.expand», «node.to.expand»>'''
		)			
	}
	
	override caseAClassTypeCG(AClassTypeCG node, StringWriter question) throws AnalysisException {
			question.append('''std::shared_ptr<«node.name»>''')
	}
	
	override caseAUnionTypeCG(AUnionTypeCG node, StringWriter question)
	{
		question.append('''«node.types.first.expand»''');
	}
		
	override caseACharBasicTypeCG(ACharBasicTypeCG node, StringWriter question) throws AnalysisException
	{
		question.append('''char''')
	} 
	
	override caseARealNumericBasicTypeCG(ARealNumericBasicTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''double''')
	}
	
	override caseARealBasicTypeWrappersTypeCG(ARealBasicTypeWrappersTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''double''')
	}
	
	override caseAIntNumericBasicTypeCG(AIntNumericBasicTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''int''')
	}
	
	override caseABoolBasicTypeCG(ABoolBasicTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''bool''')
	}
	
	override caseAStringTypeCG(AStringTypeCG node, StringWriter question) throws AnalysisException {
		question.append('''std::string''')
	}
	
	override caseANat1NumericBasicTypeCG(ANat1NumericBasicTypeCG node, StringWriter question)
	{
		question.append('''double''');
	}
}