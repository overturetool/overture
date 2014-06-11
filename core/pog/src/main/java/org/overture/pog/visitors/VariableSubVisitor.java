package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.*;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.types.AFieldField;

//FIXME complete the variable substitution visitor
public class VariableSubVisitor extends QuestionAnswerAdaptor<Substitution, PExp> implements IVariableSubVisitor {

	private IVariableSubVisitor main;

	
	public VariableSubVisitor() {
		main = this;
	}

	public VariableSubVisitor(IVariableSubVisitor main){
		this.main = main;
	}
	
	@Override
	public PExp caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			Substitution question) throws AnalysisException {
		PExp sub = node.getExp().apply(main, question);
		node.setExp(sub);
		return node;
	}
	
	@Override
	public PExp defaultSBinaryExp(SBinaryExp node, Substitution question)
			throws AnalysisException {
		PExp subl = node.getLeft().apply(main,question);
		PExp subr = node.getRight().apply(main,question);
		node.setLeft(subl);
		node.setRight(subr);
		return node.clone();
	}

	@Override
	public PExp caseAApplyExp(AApplyExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAApplyExp(node, question);
	}

	@Override
	public PExp caseABooleanConstExp(ABooleanConstExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseABooleanConstExp(node, question);
	}

	@Override
	public PExp caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseACardinalityUnaryExp(node, question);
	}

	@Override
	public PExp caseACasesExp(ACasesExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseACasesExp(node, question);
	}

	@Override
	public PExp caseACasesStm(ACasesStm node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseACasesStm(node, question);
	}

	@Override
	public PExp caseACharLiteralExp(ACharLiteralExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseACharLiteralExp(node, question);
	}

	@Override
	public PExp caseACompBinaryExp(ACompBinaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseACompBinaryExp(node, question);
	}

	@Override
	public PExp caseADefExp(ADefExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADefExp(node, question);
	}

	@Override
	public PExp caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADistConcatUnaryExp(node, question);
	}

	@Override
	public PExp caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADistIntersectUnaryExp(node, question);
	}

	@Override
	public PExp caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADistMergeUnaryExp(node, question);
	}

	@Override
	public PExp caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADistUnionUnaryExp(node, question);
	}

	@Override
	public PExp caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADivNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADivideNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADomainResByBinaryExp(node, question);
	}

	@Override
	public PExp caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseADomainResToBinaryExp(node, question);
	}

	@Override
	public PExp caseAElementsUnaryExp(AElementsUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAElementsUnaryExp(node, question);
	}

	@Override
	public PExp caseAElseIfExp(AElseIfExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAElseIfExp(node, question);
	}


	@Override
	public PExp caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAEquivalentBooleanBinaryExp(node, question);
	}

	@Override
	public PExp caseAExists1Exp(AExists1Exp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAExists1Exp(node, question);
	}

	@Override
	public PExp caseAExistsExp(AExistsExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAExistsExp(node, question);
	}

	@Override
	public PExp caseAFieldExp(AFieldExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAFieldExp(node, question);
	}

	@Override
	public PExp caseAFieldField(AFieldField node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAFieldField(node, question);
	}

	@Override
	public PExp caseAFieldNumberExp(AFieldNumberExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAFieldNumberExp(node, question);
	}

	@Override
	public PExp caseAFloorUnaryExp(AFloorUnaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAFloorUnaryExp(node, question);
	}

	@Override
	public PExp caseAForAllExp(AForAllExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAForAllExp(node, question);
	}

	@Override
	public PExp caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAGreaterEqualNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAGreaterNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseAHeadUnaryExp(AHeadUnaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAHeadUnaryExp(node, question);
	}

	@Override
	public PExp caseAHistoryExp(AHistoryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAHistoryExp(node, question);
	}

	@Override
	public PExp caseAIfExp(AIfExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAIfExp(node, question);
	}

	@Override
	public PExp caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAImpliesBooleanBinaryExp(node, question);
	}

	@Override
	public PExp caseAInSetBinaryExp(AInSetBinaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAInSetBinaryExp(node, question);
	}

	@Override
	public PExp caseAIndicesUnaryExp(AIndicesUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAIndicesUnaryExp(node, question);
	}

	@Override
	public PExp caseAIntLiteralExp(AIntLiteralExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAIntLiteralExp(node, question);
	}

	@Override
	public PExp caseAIotaExp(AIotaExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAIotaExp(node, question);
	}

	@Override
	public PExp caseAIsExp(AIsExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAIsExp(node, question);
	}

	@Override
	public PExp caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAIsOfBaseClassExp(node, question);
	}

	@Override
	public PExp caseAIsOfClassExp(AIsOfClassExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAIsOfClassExp(node, question);
	}

	@Override
	public PExp caseALambdaExp(ALambdaExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseALambdaExp(node, question);
	}

	@Override
	public PExp caseALenUnaryExp(ALenUnaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseALenUnaryExp(node, question);
	}

	@Override
	public PExp caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseALessEqualNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseALessNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseALetBeStExp(ALetBeStExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseALetBeStExp(node, question);
	}

	@Override
	public PExp caseALetDefExp(ALetDefExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseALetDefExp(node, question);
	}

	@Override
	public PExp caseAMapCompMapExp(AMapCompMapExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMapCompMapExp(node, question);
	}

	@Override
	public PExp caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMapDomainUnaryExp(node, question);
	}

	@Override
	public PExp caseAMapEnumMapExp(AMapEnumMapExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMapEnumMapExp(node, question);
	}

	@Override
	public PExp caseAMapInverseUnaryExp(AMapInverseUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMapInverseUnaryExp(node, question);
	}

	@Override
	public PExp caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMapRangeUnaryExp(node, question);
	}

	@Override
	public PExp caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMapUnionBinaryExp(node, question);
	}

	@Override
	public PExp caseAMapletExp(AMapletExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMapletExp(node, question);
	}

	@Override
	public PExp caseAMkBasicExp(AMkBasicExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMkBasicExp(node, question);
	}

	@Override
	public PExp caseAMkTypeExp(AMkTypeExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAMkTypeExp(node, question);
	}

	@Override
	public PExp caseAModNumericBinaryExp(AModNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAModNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseANarrowExp(ANarrowExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseANarrowExp(node, question);
	}

	@Override
	public PExp caseANewExp(ANewExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseANewExp(node, question);
	}

	@Override
	public PExp caseANilExp(ANilExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseANilExp(node, question);
	}

	@Override
	public PExp caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseANotEqualBinaryExp(node, question);
	}

	@Override
	public PExp caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseANotInSetBinaryExp(node, question);
	}

	@Override
	public PExp caseANotUnaryExp(ANotUnaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseANotUnaryExp(node, question);
	}

	@Override
	public PExp caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseANotYetSpecifiedExp(node, question);
	}

	@Override
	public PExp caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAOrBooleanBinaryExp(node, question);
	}

	@Override
	public PExp caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAPlusNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAPlusPlusBinaryExp(node, question);
	}

	@Override
	public PExp caseAPostOpExp(APostOpExp node, Substitution question)
			throws AnalysisException {
		PExp sub = node.getPostexpression().apply(main,question);
		node.setPostexpression(sub);
		return node;
	}

	@Override
	public PExp caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAPowerSetUnaryExp(node, question);
	}

	@Override
	public PExp caseAPreExp(APreExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAPreExp(node, question);
	}

	@Override
	public PExp caseAPreOpExp(APreOpExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAPreOpExp(node, question);
	}

	@Override
	public PExp caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAProperSubsetBinaryExp(node, question);
	}

	@Override
	public PExp caseAQuoteLiteralExp(AQuoteLiteralExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAQuoteLiteralExp(node, question);
	}

	@Override
	public PExp caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseARangeResByBinaryExp(node, question);
	}

	@Override
	public PExp caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseARangeResToBinaryExp(node, question);
	}

	@Override
	public PExp caseARealLiteralExp(ARealLiteralExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseARealLiteralExp(node, question);
	}

	@Override
	public PExp caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseARemNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseAReverseUnaryExp(AReverseUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAReverseUnaryExp(node, question);
	}

	@Override
	public PExp caseASameBaseClassExp(ASameBaseClassExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASameBaseClassExp(node, question);
	}

	@Override
	public PExp caseASameClassExp(ASameClassExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASameClassExp(node, question);
	}

	@Override
	public PExp caseASelfExp(ASelfExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASelfExp(node, question);
	}

	@Override
	public PExp caseASeqCompSeqExp(ASeqCompSeqExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASeqCompSeqExp(node, question);
	}

	@Override
	public PExp caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASeqConcatBinaryExp(node, question);
	}

	@Override
	public PExp caseASeqEnumSeqExp(ASeqEnumSeqExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASeqEnumSeqExp(node, question);
	}

	@Override
	public PExp caseASetCompSetExp(ASetCompSetExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASetCompSetExp(node, question);
	}

	@Override
	public PExp caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASetDifferenceBinaryExp(node, question);
	}

	@Override
	public PExp caseASetEnumSetExp(ASetEnumSetExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASetEnumSetExp(node, question);
	}

	@Override
	public PExp caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASetIntersectBinaryExp(node, question);
	}

	@Override
	public PExp caseASetRangeSetExp(ASetRangeSetExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASetRangeSetExp(node, question);
	}

	@Override
	public PExp caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASetUnionBinaryExp(node, question);
	}

	@Override
	public PExp caseAStarStarBinaryExp(AStarStarBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAStarStarBinaryExp(node, question);
	}

	@Override
	public PExp caseAStateInitExp(AStateInitExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAStateInitExp(node, question);
	}

	@Override
	public PExp caseAStringLiteralExp(AStringLiteralExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAStringLiteralExp(node, question);
	}

	@Override
	public PExp caseASubclassResponsibilityExp(ASubclassResponsibilityExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASubclassResponsibilityExp(node, question);
	}

	@Override
	public PExp caseASubseqExp(ASubseqExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASubseqExp(node, question);
	}

	@Override
	public PExp caseASubsetBinaryExp(ASubsetBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASubsetBinaryExp(node, question);
	}

	@Override
	public PExp caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseASubtractNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseATailUnaryExp(ATailUnaryExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseATailUnaryExp(node, question);
	}

	@Override
	public PExp caseAThreadIdExp(AThreadIdExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAThreadIdExp(node, question);
	}

	@Override
	public PExp caseATimeExp(ATimeExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseATimeExp(node, question);
	}

	@Override
	public PExp caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseATimesNumericBinaryExp(node, question);
	}

	@Override
	public PExp caseATupleExp(ATupleExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseATupleExp(node, question);
	}

	@Override
	public PExp caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAUnaryMinusUnaryExp(node, question);
	}

	@Override
	public PExp caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			Substitution question) throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAUnaryPlusUnaryExp(node, question);
	}

	@Override
	public PExp caseAUndefinedExp(AUndefinedExp node, Substitution question)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return super.caseAUndefinedExp(node, question);
	}

	@Override
	public PExp caseAVariableExp(AVariableExp node, Substitution question)
			throws AnalysisException {
		if (question.containsKey(node)) {
			return question.get(node);
		}
		return node;
	}
	
	@Override
	public PExp defaultPExp(PExp node, Substitution question)
			throws AnalysisException {
		return node;
	}

	@Override
	public PExp createNewReturnValue(Object arg0, Substitution arg1)
			throws AnalysisException {
		throw new AnalysisException(
				"Substitution visitor applied to non-expression object "
						+ arg0.toString());
	}

	@Override
	public PExp createNewReturnValue(INode arg0, Substitution arg1)
			throws AnalysisException {
		throw new AnalysisException(
				"Substitution visitor applied to non-expression object "
						+ arg0.toString());
	}

}
