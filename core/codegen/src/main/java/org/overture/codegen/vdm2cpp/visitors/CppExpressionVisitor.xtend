package org.overture.codegen.vdm2cpp.visitors

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
import org.overture.codegen.vdm2cpp.XtendAnswerStringVisitor

class CppExpressionVisitor extends XtendAnswerStringVisitor {
	
	XtendAnswerStringVisitor root;
	
	new(XtendAnswerStringVisitor root_visitor){
		root = root_visitor;
	}
	
	def expand(INode node)
	{
		return node.apply(root);
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
	
	def getElemType(ASetSetTypeCG cg)
	{
		return cg.setOf.expand;
	}
	
	def getElemType(ASeqSeqTypeCG cg)
	{
		return cg.seqOf.expand;
	}
	
	def getElemType(STypeCG cg)
	{
		if(cg instanceof ASetSetTypeCG)
		{
			return (cg as ASetSetTypeCG).setOf.expand;
		}
		else if(cg instanceof ASeqSeqTypeCG)
		{
			return (cg as ASeqSeqTypeCG).seqOf.expand;
		}
		else
		{
			return "nont"
		}
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
	
	override caseARealLiteralExpCG(ARealLiteralExpCG node)
	'''«node.value.toString»'''
	
	override caseAPostIncExpCG(APostIncExpCG node)
	'''«node.exp.expand»++'''
	
	override caseADeRefExpCG(ADeRefExpCG node)
	'''*«node.exp.expand»'''
	
	override caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
	'''«node.original»'''
	
	override caseAEqualsBinaryExpCG(AEqualsBinaryExpCG node)
	'''(«node.left») == («node.right»)'''
	
	override caseAIntLiteralExpCG(AIntLiteralExpCG node)
	'''«node.value»'''
	
	
	override caseANewExpCG(ANewExpCG node) throws AnalysisException 
	{
		if(node.type instanceof ARecordTypeCG)
		{
			return '''«node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR») '''
		}
		else if (node.type instanceof ASeqSeqTypeCG)
		{
			return '''«node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR») '''
		}
		else
		{
			return '''/*ed*/«node.type.expand»(new «node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR»)) '''
		}
	}
	
	override caseAEnumSeqExpCG(AEnumSeqExpCG node)
	'''/*eb*/«node.type.expand» {«FOR v: node.members SEPARATOR ','»«v.expand»«ENDFOR»}'''
	
	
	override caseANotUnaryExpCG(ANotUnaryExpCG node)
	'''!(«node.exp.expand»)'''
	
	override caseADivideNumericBinaryExpCG(ADivideNumericBinaryExpCG node)
	'''(«node.left.expand») / («node.right.expand»)'''
	
	override caseASubtractNumericBinaryExpCG(ASubtractNumericBinaryExpCG node)
	'''(«node.left.expand») - («node.right.expand»)'''
	
	
	override caseAHeadUnaryExpCG(AHeadUnaryExpCG node)
	'''boost::any_cast<«node.type.expand»>(vdm::seq_utils::hd(«node.exp.expand»))'''
	
	override caseATailUnaryExpCG(ATailUnaryExpCG node)
	'''vdm::seq_utils::tl(«node.exp.expand»)'''
	
	override caseANullExpCG(ANullExpCG node)
	'''NULL'''
	
	override caseASeqConcatBinaryExpCG(ASeqConcatBinaryExpCG node)
	'''vdm::seq_utils::concat(«node.left.expand», «node.right.expand»)'''
	
	override caseASetUnionBinaryExpCG(ASetUnionBinaryExpCG node)
	'''vdm::set_utils::union(«node.left.expand», «node.right.expand»)'''
	
	override caseAElemsUnaryExpCG(AElemsUnaryExpCG node)
	'''vdm::seq_utils::to_set<«node.type.getElemType»>(«node.exp.expand»)/*tes*/'''
	
	
	
	override caseAExplicitVarExpCG(AExplicitVarExpCG node)
	'''«IF node.classType != null»«node.classType.getGetStaticCall»::«ENDIF»«node.name»'''
	
	override caseATimesNumericBinaryExpCG(ATimesNumericBinaryExpCG node)
	'''(«node.left.expand») * («node.right.expand»)'''
	
	override caseAAndBoolBinaryExpCG(AAndBoolBinaryExpCG node)
	'''(«node.left.expand») && («node.right.expand»)'''
	
	override caseALessNumericBinaryExpCG(ALessNumericBinaryExpCG node)
	'''(«node.left.expand») < («node.right.expand»)'''
	
	override caseACastUnaryExpCG(ACastUnaryExpCG node) throws AnalysisException {
		if(node.exp.type instanceof ASeqSeqTypeCG ||
			node.exp.type instanceof AMapMapTypeCG ||
			node.exp.type instanceof ASetSetTypeCG
		)
		{
			return '''boost::any_cast<«node.type.expand»>( «node.exp.expand»)'''
		}
		else
		{
			return '''(«node.type.expand») «node.exp.expand»'''
		}
	}
	
	override caseAGreaterEqualNumericBinaryExpCG(AGreaterEqualNumericBinaryExpCG node)
	'''(«node.left.expand») >= («node.right.expand»)'''
	
	override caseALessEqualNumericBinaryExpCG(ALessEqualNumericBinaryExpCG node)
	'''(«node.left.expand») <= («node.right.expand»)'''
	
	override caseAGreaterNumericBinaryExpCG(AGreaterNumericBinaryExpCG node)
	'''(«node.left.expand») > («node.right.expand»)'''
	
	override caseAPlusNumericBinaryExpCG(APlusNumericBinaryExpCG node)
	'''(«node.left.expand») + («node.right.expand»)'''
	
	override caseANotEqualsBinaryExpCG(ANotEqualsBinaryExpCG node)
	'''(«node.left.expand») != («node.right.expand»)'''
	
	override caseAFieldExpCG(AFieldExpCG node) throws AnalysisException {
		if(node.object.type instanceof AClassTypeCG)
		{
			return '''«node.object.expand»->«node.memberName»'''
		}
		else
		{
			return '''«node.object».«node.memberName»'''
		}
	}
	
	override caseAStringLiteralExpCG(AStringLiteralExpCG node)
	'''"«StringEscapeUtils.escapeJava( node.value)»"'''
	
	override caseALenUnaryExpCG(ALenUnaryExpCG node)
	''' («node.exp»).size() '''
	
	override caseAMinusUnaryExpCG(AMinusUnaryExpCG node)
	'''-(«node.exp.expand»)'''
	
	
	override caseAAbsUnaryExpCG(AAbsUnaryExpCG node)
	'''fabs(«node.exp.expand»)'''
	
	override caseAEnumSetExpCG(AEnumSetExpCG node)
	'''«node.type.expand»::from_list( {«FOR member : node.members SEPARATOR ','» «member.expand»«ENDFOR»})'''
	
	override caseABoolLiteralExpCG(ABoolLiteralExpCG node)
	'''«node.value»'''
	
	override caseAApplyExpCG(AApplyExpCG node) throws AnalysisException {
		if(node.root.type instanceof SSeqTypeCG && node.args.length == 1)
		{
			if(node.args.head instanceof AIntLiteralExpCG)
			{
				var v = node.args.head as AIntLiteralExpCG
				return '''boost::any_cast<«node.type.expand»>(«node.root.expand».at(«FOR n : node.args SEPARATOR ','»«v.value-1»«ENDFOR»))'''	
			}
			else
			{
				return '''«node.root.expand».at(«FOR n : node.args SEPARATOR ','»(«n.expand»)-1«ENDFOR»)'''
			}
		}
		else
		{
			return '''«node.root.expand»(«FOR n : node.args SEPARATOR ','»«n.expand»«ENDFOR»)'''	
		}
	}
	
	override caseAUndefinedExpCG(AUndefinedExpCG node)
	'''0/*fixme: undefined_expression*/'''
	
	override caseAFieldNumberExpCG(AFieldNumberExpCG node)
	'''(«node.type.expand»)«node.tuple.expand».get(«node.field-1»)'''
	
	override caseATupleCompatibilityExpCG(ATupleCompatibilityExpCG node)
	'''«node.tuple».compatability(«FOR t : node.types SEPARATOR ","»«t.expand»«ENDFOR»)'''
	
	override caseAMethodInstantiationExpCG(AMethodInstantiationExpCG node)
	'''«node.func.expand»'''
	
	override caseAStringToSeqUnaryExpCG(AStringToSeqUnaryExpCG node)
	'''string_util::to_seq(«node.exp.expand»)'''
	
	
	
	
	
}