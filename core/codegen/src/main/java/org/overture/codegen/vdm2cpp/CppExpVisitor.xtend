package org.overture.codegen.vdm2cpp

import java.io.StringWriter
import org.apache.commons.lang.StringEscapeUtils
import org.overture.codegen.cgast.INode
import org.overture.codegen.cgast.SExpCGBase
import org.overture.codegen.cgast.STypeCG
import org.overture.codegen.cgast.analysis.AnalysisException
import org.overture.codegen.cgast.declarations.AClassDeclCG
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG
import org.overture.codegen.cgast.expressions.AApplyExpCG
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG
import org.overture.codegen.cgast.expressions.ADeRefExpCG
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG
import org.overture.codegen.cgast.expressions.AEnumSetExpCG
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG
import org.overture.codegen.cgast.expressions.AFieldExpCG
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AMethodInstantiationExpCG
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG
import org.overture.codegen.cgast.expressions.ANewExpCG
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG
import org.overture.codegen.cgast.expressions.ANullExpCG
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.APostIncExpCG
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG
import org.overture.codegen.cgast.expressions.ASetUnionBinaryExpCG
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG
import org.overture.codegen.cgast.expressions.AStringToSeqUnaryExpCG
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.ATailUnaryExpCG
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.ATupleCompatibilityExpCG
import org.overture.codegen.cgast.expressions.AUndefinedExpCG
import org.overture.codegen.cgast.types.AClassTypeCG
import org.overture.codegen.cgast.types.AMapMapTypeCG
import org.overture.codegen.cgast.types.ARecordTypeCG
import org.overture.codegen.cgast.types.ASeqSeqTypeCG
import org.overture.codegen.cgast.types.ASetSetTypeCG
import org.overture.codegen.cgast.types.SSeqTypeCG
import org.overture.codegen.merging.MergeVisitor
import org.overture.codegen.merging.TemplateCallable
import org.overture.codegen.merging.TemplateStructure
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG

class CppExpVisitor extends MergeVisitor {
	
	vdm2cppGen root_generator;
	
	new(vdm2cppGen root,TemplateStructure templateStructure, TemplateCallable[] templateCallables) {
		super(templateStructure, templateCallables)
		
		root_generator = root
		
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
	
	def expand(INode node)
	{
		var str = new StringWriter()
		node.apply(this,str)
		return str.toString()
	}
	
	override defaultINode(INode node, StringWriter question) throws AnalysisException {
		
		if( node instanceof SExpCGBase )
		{
			System.out.println("unhandled expression node: " + node.getClass.toString() )
			System.out.println( (node as SExpCGBase).tag)
			System.out.println("In Class: " + node.getAncestor(AClassDeclCG).name)
			question.append('''/*unhandled exp «node.getClass.toString()»*/''')
		}
		else
		{
			node.apply(root_generator,question)
		}
	}
	
	override caseARealLiteralExpCG(ARealLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.value.toString»''')
	}

	override caseAIsolationUnaryExpCG(AIsolationUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.exp.expand»)''')
	}
	
	override caseAPostIncExpCG(APostIncExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.exp.expand»++''');
	}
	
	override caseADeRefExpCG(ADeRefExpCG node, StringWriter question) throws AnalysisException {
		question.append('''*«node.exp.expand»''');
	}
	
	override caseAIdentifierVarExpCG(AIdentifierVarExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.original»''')
	}
	
	override caseAEqualsBinaryExpCG(AEqualsBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left» == «node.right»''')
	}
	
	override caseAIntLiteralExpCG(AIntLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.value»''')
	}
	
	override caseANewExpCG(ANewExpCG node, StringWriter question) throws AnalysisException 
	{
		if(node.type instanceof ARecordTypeCG)
		{
			question.append('''«node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR») ''')
		}
		else if (node.type instanceof ASeqSeqTypeCG)
		{
			question.append('''«node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR») ''')
		}
		else
		{
			question.append('''/*ed*/«node.type.expand»(new «node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR»)) ''')
		}
	}
	
	override caseAEnumSeqExpCG(AEnumSeqExpCG node, StringWriter question) throws AnalysisException {
		question.append(
			'''/*eb*/«node.type.expand» {«FOR v: node.members SEPARATOR ','»«v.expand»«ENDFOR»}'''
		)
	}
	
		override caseANotUnaryExpCG(ANotUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''!(«node.exp.expand»)''')
	}
	
	override caseADivideNumericBinaryExpCG(ADivideNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.left.expand») / («node.right.expand»)''')
	}
	
	override caseASubtractNumericBinaryExpCG(ASubtractNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.left.expand») - («node.right.expand»)''')
	}
	
	override caseAHeadUnaryExpCG(AHeadUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''boost::any_cast<«node.type.expand»>(vdm::seq_utils::hd(«node.exp.expand»))''')
	}
	
	
	override caseATailUnaryExpCG(ATailUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''vdm::seq_utils::tl(«node.exp.expand»)''')
	}
	
	override caseANullExpCG(ANullExpCG node, StringWriter question) throws AnalysisException {
		question.append('''NULL''')	
	}
	
	override caseASeqConcatBinaryExpCG(ASeqConcatBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''vdm::seq_utils::concat(«node.left.expand», «node.right.expand»)''')
	}
	
	override caseASetUnionBinaryExpCG(ASetUnionBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''vdm::set_utils::union(«node.left.expand», «node.right.expand»)''')
	}
	
	override caseAElemsUnaryExpCG(AElemsUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''/*TODO*/«node.exp.expand»''')
	}
	
	override caseAExplicitVarExpCG(AExplicitVarExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«IF node.classType != null»«node.classType.getStaticCall»::«ENDIF»«node.name»''')
	}
	
	override caseATimesNumericBinaryExpCG(ATimesNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» * «node.right.expand»''');
	}
	
	override caseAAndBoolBinaryExpCG(AAndBoolBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» && «node.right.expand»''')
	}
	
	override caseALessNumericBinaryExpCG(ALessNumericBinaryExpCG node, StringWriter question)
	{
		question.append('''(«node.left.expand» < «node.right.expand»)''')
	}
	
	override caseACastUnaryExpCG(ACastUnaryExpCG node, StringWriter question) throws AnalysisException {
		if(node.exp.type instanceof ASeqSeqTypeCG ||
			node.exp.type instanceof AMapMapTypeCG ||
			node.exp.type instanceof ASetSetTypeCG
		)
		{
			question.append('''boost::any_cast<(«node.type.expand»)>( «node.exp.expand»)''')
		}
		else
		{
			question.append('''(«node.type.expand») «node.exp.expand»''')
		}
	}
	
	override caseAGreaterEqualNumericBinaryExpCG(AGreaterEqualNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» >= «node.right.expand»''')
	}
	
	override caseALessEqualNumericBinaryExpCG(ALessEqualNumericBinaryExpCG node, StringWriter question)
	{
		question.append('''( «node.left.expand» <= «node.right.expand» )''')
	}
	
	override caseAGreaterNumericBinaryExpCG(AGreaterNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» > «node.right.expand»''')
	}
	
	override caseAPlusNumericBinaryExpCG(APlusNumericBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.left.expand» + «node.right.expand»''')
	}
	
	override caseANotEqualsBinaryExpCG(ANotEqualsBinaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.left.expand») != («node.right.expand»)''')
	}
	
	override caseAFieldExpCG(AFieldExpCG node, StringWriter question) throws AnalysisException {
		if(node.object.type instanceof AClassTypeCG)
		{
			question.append('''«node.object.expand»->«node.memberName»''');
		}
		else
		{
			question.append('''«node.object».«node.memberName»''');
		}
	}
	
	override caseAStringLiteralExpCG(AStringLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''"«StringEscapeUtils.escapeJava( node.value)»"''')
	}
	
	override caseALenUnaryExpCG(ALenUnaryExpCG node, StringWriter question)
	{
		question.append(''' («node.exp»).size() ''')
	}
	
	override caseAMinusUnaryExpCG(AMinusUnaryExpCG node, StringWriter question)
	{
		question.append('''-(«node.exp.expand»)''')
	}
	
	override caseAAbsUnaryExpCG(AAbsUnaryExpCG node, StringWriter question)
	{
		question.append('''fabs(«node.exp.expand»)''')
	}
	
	override caseAEnumSetExpCG(AEnumSetExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.type.expand»::from_list( {«FOR member : node.members SEPARATOR ','» «member.expand»«ENDFOR»})''')
	}
	
	override caseABoolLiteralExpCG(ABoolLiteralExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.value»''')
	}
	
	override caseAApplyExpCG(AApplyExpCG node, StringWriter question) throws AnalysisException {
		if(node.root.type instanceof SSeqTypeCG && node.args.length == 1)
		{
			if(node.args.head instanceof AIntLiteralExpCG)
			{
				var v = node.args.head as AIntLiteralExpCG
				 
				question.append('''boost::any_cast<«node.type.expand»>(«node.root.expand».at(«FOR n : node.args SEPARATOR ','»«v.value-1»«ENDFOR»))''')	
			}
			else
			{
				question.append('''«node.root.expand».at(«FOR n : node.args SEPARATOR ','»(«n.expand»)-1«ENDFOR»)''')
			}
		}
		else
		{
			question.append('''«node.root.expand»(«FOR n : node.args SEPARATOR ','»«n.expand»«ENDFOR»)''')	
		}
	}
	
	override caseAUndefinedExpCG(AUndefinedExpCG node, StringWriter question) throws AnalysisException {
		question.append('''0/*fixme: undefined_expression*/''')
	}
	
	override caseAFieldNumberExpCG(AFieldNumberExpCG node, StringWriter question) throws AnalysisException {
		question.append('''(«node.type.expand»)«node.tuple.expand».get(«node.field-1»)''')
	}
	
	override caseATupleCompatibilityExpCG(ATupleCompatibilityExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.tuple».compatability(«FOR t : node.types SEPARATOR ","»«t.expand»«ENDFOR»)''')
		
	}
	
	override caseAMethodInstantiationExpCG(AMethodInstantiationExpCG node, StringWriter question) throws AnalysisException {
		question.append('''«node.func.expand»''')
	}
	
	override caseAStringToSeqUnaryExpCG(AStringToSeqUnaryExpCG node, StringWriter question) throws AnalysisException {
		question.append('''string_util::to_seq(«node.exp.expand»)''')
	}
	
}