package org.overture.codegen.vdm2cpp.vdmtools

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
	'''Real(«node.value.toString»)'''
	
	override caseAPostIncExpCG(APostIncExpCG node)
	'''«node.exp.expand»++'''
	
	override caseADeRefExpCG(ADeRefExpCG node)
	'''*«node.exp.expand»'''
	
	override caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
	'''«node.name»'''
	
	override caseAEqualsBinaryExpCG(AEqualsBinaryExpCG node)
	'''(«node.left.expand») == («node.right.expand»)'''
	
	override caseAIntLiteralExpCG(AIntLiteralExpCG node)
	'''Int(«node.value»)'''
	
	
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
			return '''type_ref_«node.name.expand»(new «node.name.expand»(«FOR a : node.args SEPARATOR ','»«a.expand»«ENDFOR»)) '''
		}
	}
	
	override caseAEnumSeqExpCG(AEnumSeqExpCG node)
	{
		if(node.members.length > 0)
		{
			'''mk_sequence(«FOR v: node.members SEPARATOR ','»«v.expand»«ENDFOR»)'''		
		}
		else
		{
			'''Sequence()'''
		}
	}
	
	
	
	override caseANotUnaryExpCG(ANotUnaryExpCG node)
	'''!(«node.exp.expand»)'''
	
	override caseADivideNumericBinaryExpCG(ADivideNumericBinaryExpCG node)
	'''(«node.left.expand») / («node.right.expand»)'''
	
	override caseASubtractNumericBinaryExpCG(ASubtractNumericBinaryExpCG node)
	'''(«node.left.expand») - («node.right.expand»)'''
	
	
	override caseAHeadUnaryExpCG(AHeadUnaryExpCG node)
	'''«node.type.caseToType»((«node.exp.expand»).Hd())'''
	
	override caseATailUnaryExpCG(ATailUnaryExpCG node)
	'''(«node.exp.expand»).Tl()'''
	
	override caseANullExpCG(ANullExpCG node)
	'''Nil()'''
	
	override caseASeqConcatBinaryExpCG(ASeqConcatBinaryExpCG node)
	'''«node.left.expand».Conc(«node.right.expand»)'''
	
	override caseASetUnionBinaryExpCG(ASetUnionBinaryExpCG node)
	'''/*vdm::set_utils::union*/(«node.left.expand»).Union(«node.right.expand»)'''
	
	override caseAElemsUnaryExpCG(AElemsUnaryExpCG node)
	'''(«node.exp.expand»).Elems()'''
	
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
			val class_name = (node.object.type as AClassTypeCG).name
			return '''ObjGet_«class_name»(«node.object.expand»)->«node.memberName»'''
		}
		else
		{
			return '''«node.object».«node.memberName»'''
		}
	}
	
	override caseAStringLiteralExpCG(AStringLiteralExpCG node)
	'''"«StringEscapeUtils.escapeJava( node.value)»"'''
	
	override caseALenUnaryExpCG(ALenUnaryExpCG node)
	''' («node.exp»).Length() '''
	
	override caseAMinusUnaryExpCG(AMinusUnaryExpCG node)
	'''-(«node.exp.expand»)'''
	
	
	override caseAAbsUnaryExpCG(AAbsUnaryExpCG node)
	'''CGUTIL::RAbs(«node.exp.expand»)'''
	
	override caseAEnumSetExpCG(AEnumSetExpCG node)
	'''«node.type.expand»::from_list( {«FOR member : node.members SEPARATOR ','» «member.expand»«ENDFOR»})'''
	
	override caseABoolLiteralExpCG(ABoolLiteralExpCG node)
	'''Bool(«node.value»)'''
	
	override caseAApplyExpCG(AApplyExpCG node) throws AnalysisException {
		if(node.root.type instanceof SSeqTypeCG && node.args.length == 1)
		{
			if(node.args.head instanceof AIntLiteralExpCG)
			{
				var v = node.args.head as AIntLiteralExpCG
				return '''«node.type.caseToType»(«node.root.expand»[«FOR n : node.args SEPARATOR ','»«v.value»«ENDFOR»])'''	
			}
			else
			{
				return '''«node.root.expand»[«FOR n : node.args SEPARATOR ','»(«n.expand»)«ENDFOR»]'''
			}
		}
		else
		{
			return '''«node.root.expand»(«FOR n : node.args SEPARATOR ','»«n.expand»«ENDFOR»)'''	
		}
	}
	
	override caseAUndefinedExpCG(AUndefinedExpCG node)
	'''Nil()/*fixme: undefined_expression*/'''
	
	override caseAFieldNumberExpCG(AFieldNumberExpCG node)
	'''«node.tuple.expand».Get«node.type.expand»(«node.field»)'''
	
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
	
}