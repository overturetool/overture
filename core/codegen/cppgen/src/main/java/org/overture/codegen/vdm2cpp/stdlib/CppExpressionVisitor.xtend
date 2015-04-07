package org.overture.codegen.vdm2cpp.stdlib

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
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG
import org.overture.codegen.cgast.types.ARecordTypeCG
import org.overture.codegen.cgast.types.ASeqSeqTypeCG
import org.overture.codegen.cgast.types.ASetSetTypeCG
import org.overture.codegen.cgast.types.SSeqTypeCG
import org.overture.codegen.vdm2cpp.XtendAnswerStringVisitor
import org.overture.codegen.cgast.expressions.AAssignExpExpCG
import org.overture.codegen.cgast.types.ATupleTypeCG
import org.overture.codegen.cgast.types.AMethodTypeCG
import org.overture.codegen.cgast.expressions.APatternMatchRuntimeErrorExpCG
import org.overture.codegen.cgast.expressions.APreIncExpCG
import org.overture.codegen.cgast.expressions.AMapDomainUnaryExpCG
import org.overture.codegen.cgast.expressions.AMapletExpCG
import org.overture.codegen.cgast.expressions.AMapRangeUnaryExpCG
import org.overture.codegen.cgast.expressions.AMapInverseUnaryExpCG
import org.overture.codegen.cgast.expressions.ATupleExpCG
import org.overture.codegen.cgast.expressions.ASubSeqExpCG
import org.overture.codegen.cgast.expressions.ASelfExpCG
import org.overture.codegen.cgast.expressions.APowerNumericBinaryExpCG
import org.overture.codegen.cgast.expressions.AIndicesUnaryExpCG
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG
import org.overture.codegen.cgast.expressions.ASetDifferenceBinaryExpCG

class CppExpressionVisitor extends XtendAnswerStringVisitor {
	
	XtendAnswerStringVisitor root;
	
	new(XtendAnswerStringVisitor root_visitor){
		root = root_visitor;
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
//		else if (type instanceof ASeqSeqTypeCG)
//		{
//			
//		}
		else
		{
			return '''static_cast<«type.expand»>'''
		}
		
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
	
	override caseAPreIncExpCG(APreIncExpCG node)
	'''++«node.exp.expand»'''
	
	override caseADeRefExpCG(ADeRefExpCG node)
	'''*«node.exp.expand»'''
	
	override caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
	'''«node.name»'''
	
	override caseAEqualsBinaryExpCG(AEqualsBinaryExpCG node)
	'''(«node.left.expand») == («node.right.expand»)'''
	
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
			return '''«node.type.expand»(new «node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR»)) '''
		}
	}
	
	override caseAEnumSeqExpCG(AEnumSeqExpCG node)
	{
		if(node.members.length > 0)
		{
			'''{«FOR v: node.members SEPARATOR ','»«v.expand»«ENDFOR»}'''		
		}
		else
		{
			'''{}'''
		}
	}
	
	
	
	override caseANotUnaryExpCG(ANotUnaryExpCG node)
	'''!(«node.exp.expand»)'''
	
	override caseADivideNumericBinaryExpCG(ADivideNumericBinaryExpCG node)
	'''(«node.left.expand») / («node.right.expand»)'''
	
	override caseASubtractNumericBinaryExpCG(ASubtractNumericBinaryExpCG node)
	'''(«node.left.expand») - («node.right.expand»)'''
	
	
	override caseAHeadUnaryExpCG(AHeadUnaryExpCG node)
	'''vdm::head(«node.exp.expand»)'''
	
	override caseATailUnaryExpCG(ATailUnaryExpCG node)
	'''vdm::tail(«node.exp.expand»)'''
	
	override caseANullExpCG(ANullExpCG node)
	'''Nil()'''
	
	override caseASeqConcatBinaryExpCG(ASeqConcatBinaryExpCG node)
	'''vdm::concat(«node.left.expand»,«node.right.expand»)'''
	
	override caseASetUnionBinaryExpCG(ASetUnionBinaryExpCG node)
	'''vdm::set_utils::union(«node.left.expand»,«node.right.expand»)'''
	
	override caseASetDifferenceBinaryExpCG(ASetDifferenceBinaryExpCG node)
	'''vdm::set_utils::difference(«node.left.expand»,«node.right.expand»)'''
	
	override caseAElemsUnaryExpCG(AElemsUnaryExpCG node)
	'''vdm::elems<«node.type.expand»>(«node.exp.expand»)'''
	
	def getTupleTypes(STypeCG node)
	{
		//
		if(node instanceof AMethodTypeCG)
		{
			var tp = node as AMethodTypeCG
			//System.out.println(tp.)
			System.out.println(tp.class)
			System.out.println(tp.result)
			System.out.println(tp.params.class)
			
			return '''<«FOR p : tp.params SEPARATOR ','»«p.expand» «ENDFOR»>'''
		}
		return null;
	}
	
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
			return '''«node.type.caseToType»( «node.exp.expand»)'''
		}
		else
		{
			return '''«node.type.caseToType»(«node.exp.expand»)'''
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
			return '''(«node.object.expand»)->«node.memberName»'''
		}
		else if (node.object.type == null)
		{
			return '''«node.object.expand»->«node.memberName»'''
		}
		else
		{
			return '''«node.object.expand».«node.memberName»'''
		}
	}
	
	override caseAStringLiteralExpCG(AStringLiteralExpCG node)
	'''"«StringEscapeUtils.escapeJava( node.value)»"'''
	
	override caseALenUnaryExpCG(ALenUnaryExpCG node)
	''' vdm::len(«node.exp.expand»)'''
	
	override caseAMinusUnaryExpCG(AMinusUnaryExpCG node)
	'''-(«node.exp.expand»)'''
	
	
	override caseAAbsUnaryExpCG(AAbsUnaryExpCG node)
	'''fabs(«node.exp.expand»)'''
	
	override caseAEnumSetExpCG(AEnumSetExpCG node)
	'''vdm::from_list<«node.type.expand»>( {«FOR member : node.members SEPARATOR ','» «member.expand»«ENDFOR»})'''
	
	override caseABoolLiteralExpCG(ABoolLiteralExpCG node)
	'''«node.value»'''
	
	override caseAApplyExpCG(AApplyExpCG node) throws AnalysisException {
		if(node.root.type instanceof SSeqTypeCG && node.args.length == 1)
		{
			if(node.args.head instanceof AIntLiteralExpCG)
			{
				var v = node.args.head as AIntLiteralExpCG
				return '''«node.root.expand»[«FOR n : node.args SEPARATOR ','»«v.value-1»«ENDFOR»]/*c1*/'''	
			}
			else
			{
				return '''«node.root.expand»[«FOR n : node.args SEPARATOR ','»(«n.expand»)-1«ENDFOR»]/*c2*/'''
			}
		}
		else
		{
			return '''«node.root.expand»(«FOR n : node.args SEPARATOR ','»«n.expand»«ENDFOR»)/*c3*/'''	
		}
	}
	
	override caseAUndefinedExpCG(AUndefinedExpCG node)
	'''Nil()/*fixme: undefined_expression*/'''
	
	override caseAFieldNumberExpCG(AFieldNumberExpCG node)
	'''std::get<«node.field-1»>(«node.tuple.expand»)'''
	
	override caseATupleCompatibilityExpCG(ATupleCompatibilityExpCG node)
	'''vdm::compatible<«FOR t : node.types SEPARATOR ","»«t.expand»«ENDFOR»>(«node.tuple»)'''
	
	override caseAMethodInstantiationExpCG(AMethodInstantiationExpCG node)
	'''«node.func.expand»'''
	
	override caseAStringToSeqUnaryExpCG(AStringToSeqUnaryExpCG node)
	'''(«node.exp.expand»)/*«node.exp.type.expand», «node.type.expand»*/'''
	
	
	override caseAAssignExpExpCG(AAssignExpExpCG node)
	'''«node.target.expand» = «node.value.expand»'''
	
	override caseAPatternMatchRuntimeErrorExpCG(APatternMatchRuntimeErrorExpCG node)
	'''«node.message»'''
	
	override caseAMapletExpCG(AMapletExpCG node)
	'''std::pair<«node.left.type.expand», «node.right.type.expand»>(«node.left.expand», «node.right.expand»)'''
	
	override caseAMapDomainUnaryExpCG(AMapDomainUnaryExpCG node)
	'''vdm::dom(«node.exp.expand»)'''

	override caseAMapRangeUnaryExpCG(AMapRangeUnaryExpCG node)
	'''vdm::rng(«node.exp.expand»)'''
	
	override caseAMapInverseUnaryExpCG(AMapInverseUnaryExpCG node)
	'''vdm::map_inverse(«node.exp.expand»)'''
	
	
	override caseATupleExpCG(ATupleExpCG node)
	'''std::make_tuple(«FOR arg : node.args SEPARATOR ','»«arg.expand»«ENDFOR»)'''

	override caseASubSeqExpCG(ASubSeqExpCG node)
	'''vdm::subseq(«node.seq.expand»,«node.from.expand»,«node.to.expand»)'''
	
	
	override caseASelfExpCG(ASelfExpCG node)'''this'''
	
	override caseAPowerNumericBinaryExpCG(APowerNumericBinaryExpCG node)'''pow(«node.left.expand»,«node.right.expand»)'''
	
	override caseAIndicesUnaryExpCG(AIndicesUnaryExpCG node)
	'''vdm::inds(«node.exp.expand»)''' 
	
	override caseACharLiteralExpCG(ACharLiteralExpCG node) 
	''' '«node.value»' '''
	
}