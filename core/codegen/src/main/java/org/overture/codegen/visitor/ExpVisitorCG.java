package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.ADistConcatUnaryExp;
import org.overture.ast.expressions.ADistIntersectUnaryExp;
import org.overture.ast.expressions.ADistMergeUnaryExp;
import org.overture.ast.expressions.ADistUnionUnaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AFloorUnaryExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.AIsOfClassExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.AReverseUnaryExp;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetDifferenceBinaryExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetIntersectBinaryExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.ASetUnionBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.AUnaryPlusUnaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACompMapExpCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.ACompSetExpCG;
import org.overture.codegen.cgast.expressions.ADistConcatUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADistIntersectUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADistMergeUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADistUnionUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ADomainResByBinaryExpCG;
import org.overture.codegen.cgast.expressions.ADomainResToBinaryExpCG;
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEnumMapExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExists1QuantifierExpCG;
import org.overture.codegen.cgast.expressions.AExistsQuantifierExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AFloorUnaryExpCG;
import org.overture.codegen.cgast.expressions.AForAllQuantifierExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AHeadUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInSetBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIndicesUnaryExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.ALambdaExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.AMapDomainUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapInverseUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapOverrideBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMapRangeUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapUnionBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.expressions.AMethodInstantiationExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMkBasicExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.APowerNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APowerSetUnaryExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.ARangeResByBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARangeResToBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARangeSetExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.AReverseUnaryExpCG;
import org.overture.codegen.cgast.expressions.ASelfExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASeqModificationBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetDifferenceBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetIntersectBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetProperSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetUnionBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASizeUnaryExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATailUnaryExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.expressions.AXorBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetBindCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.patterns.PBindCG;
import org.overture.codegen.cgast.patterns.PMultipleBindCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class ExpVisitorCG extends AbstractVisitorCG<IRInfo, PExpCG>
{
	@Override
	public PExpCG caseANilExp(ANilExp node, IRInfo question)
			throws AnalysisException
	{
		return new ANullExpCG();
	}
	
	@Override
	public PExpCG caseAMkBasicExp(AMkBasicExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		if(!(type instanceof ATokenBasicType))
		{
			question.addUnsupportedNode(node, "Expected token type for mk basic expression. Got: " + type);
			return null;
		}
		
		PExp arg = node.getArg();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG argCg = arg.apply(question.getExpVisitor(), question);

		AMkBasicExpCG mkBasicExp = new AMkBasicExpCG();
		mkBasicExp.setType(typeCg);
		mkBasicExp.setArg(argCg);
		
		return mkBasicExp;
	}
	
	@Override
	public PExpCG caseAIsOfClassExp(AIsOfClassExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		AClassType classType = node.getClassType();
		PExp objRef = node.getExp();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PTypeCG classTypeCg = classType.apply(question.getTypeVisitor(), question);

		if (!(classTypeCg instanceof AClassTypeCG))
			throw new AnalysisExceptionCG("Unexpected class type encountered for "
					+ AIsOfClassExp.class.getName() + ". Expected class type: "
					+ AClassTypeCG.class.getName() + ". Got: " + typeCg.getClass().getName(), node.getLocation());

		PExpCG objRefCg = objRef.apply(question.getExpVisitor(), question);

		AInstanceofExpCG instanceOfExp = new AInstanceofExpCG();
		instanceOfExp.setType(typeCg);
		instanceOfExp.setClassType((AClassTypeCG) classTypeCg);
		instanceOfExp.setObjRef(objRefCg);
		
		return instanceOfExp;
	}
	
	@Override
	public PExpCG caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ASizeUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAInSetBinaryExp(AInSetBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node,  new AInSetBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().negate(question.getExpAssistant().handleBinaryExp(node,  new AInSetBinaryExpCG(), question));
	}
	
	@Override
	public PExpCG caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node,  new ASetUnionBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetIntersectBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetDifferenceBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASubsetBinaryExp(ASubsetBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetSubsetBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetProperSubsetBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistUnionUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistIntersectUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new APowerSetUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASetEnumSetExp(ASetEnumSetExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		if(!(type instanceof ASetType))
			throw new AnalysisExceptionCG("Unexpected set type for set enumeration expression: " + type.getClass().getName(), node.getLocation());
		
		LinkedList<PExp> members = node.getMembers();
		
		AEnumSetExpCG enumSet = new AEnumSetExpCG();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		enumSet.setType(typeCg);
		LinkedList<PExpCG> membersCg = enumSet.getMembers();
		
		for(PExp member : members)
		{
			membersCg.add(member.apply(question.getExpVisitor(), question));
		}
	
		return enumSet;
	}

	@Override
	public PExpCG caseAForAllExp(AForAllExp node, IRInfo question)
			throws AnalysisException
	{
		//The inheritance hierarchy of the VDM AST tree is structured such that the bindings and the predicate
		//must also be passed to the method that handles the forall and the exists quantifiers
		return question.getExpAssistant().handleQuantifier(node, node.getBindList(), node.getPredicate(), new AForAllQuantifierExpCG(), question, "forall expression");
	}
	
	@Override
	public PExpCG caseAExistsExp(AExistsExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleQuantifier(node, node.getBindList(), node.getPredicate(), new AExistsQuantifierExpCG(), question, "exists expression");
	}
	
	@Override
	public PExpCG caseAExists1Exp(AExists1Exp node, IRInfo question)
			throws AnalysisException
	{
		PBind bind = node.getBind();

		if(!(bind instanceof ASetBind))
		{
			question.addUnsupportedNode(node, String.format("Generation of a exist1 expression is only supported for set binds. Got: %s", bind));
			return null;
		}

		PBindCG bindCg = bind.apply(question.getBindVisitor(), question);

		if (!(bindCg instanceof ASetBindCG))
		{
			question.addUnsupportedNode(node, String.format("Generation of 	a set bind was expected to yield a ASetBindCG. Got: %s", bindCg));
			return null;
		}
		
		ASetBindCG setBind = (ASetBindCG) bindCg;
		
		PType type = node.getType();
		PExp predicate = node.getPredicate();

		ASetMultipleBindCG multipleSetBind = question.getBindAssistant().convertToMultipleSetBind(setBind);
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG predicateCg = predicate.apply(question.getExpVisitor(), question);
		
		AExists1QuantifierExpCG exists1Exp = new AExists1QuantifierExpCG();
		exists1Exp.getBindList().add(multipleSetBind);
		exists1Exp.setType(typeCg);
		exists1Exp.setPredicate(predicateCg);
		
		return exists1Exp;
	}

	@Override
	public PExpCG caseASetCompSetExp(ASetCompSetExp node, IRInfo question)
			throws AnalysisException
	{
		if (question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, "Generation of a set comprehension is only supported within operations/functions");
			return null;
		}
		
		LinkedList<PMultipleBind> bindings = node.getBindings();

		LinkedList<ASetMultipleBindCG> bindingsCg = new LinkedList<ASetMultipleBindCG>();
		for (PMultipleBind multipleBind : bindings)
		{
			if(!(multipleBind instanceof ASetMultipleBind))
			{
				question.addUnsupportedNode(node, "Generation of a set comprehension is only supported for multiple set binds. Got: "
						+ multipleBind);
				return null;
			}
			
			PMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
			
			if (!(multipleBindCg instanceof ASetMultipleBindCG))
			{
				question.addUnsupportedNode(node, "Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: " + multipleBindCg);
			}
			
			bindingsCg.add((ASetMultipleBindCG) multipleBindCg);
		}
		
		PType type = node.getType();
		PExp first = node.getFirst();
		PExp predicate = node.getPredicate();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG firstCg = first.apply(question.getExpVisitor(), question);
		PExpCG predicateCg = predicate != null ? predicate.apply(question.getExpVisitor(), question) : null;

		ACompSetExpCG setComp = new ACompSetExpCG();
		setComp.setBindings(bindingsCg);
		setComp.setType(typeCg);
		setComp.setFirst(firstCg);
		setComp.setPredicate(predicateCg);
		
		return setComp;
	}
	
	@Override
	public PExpCG caseASetRangeSetExp(ASetRangeSetExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp firstExp = node.getFirst();
		PExp lastExp = node.getLast();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG firstExpCg = firstExp.apply(question.getExpVisitor(), question);
		PExpCG lastExpCg = lastExp.apply(question.getExpVisitor(), question);
		
		ARangeSetExpCG setRange = new ARangeSetExpCG();
		
		setRange.setType(typeCg);
		setRange.setFirst(firstExpCg);
		setRange.setLast(lastExpCg);
		
		return setRange;
	}
	
	@Override
	public PExpCG caseAIfExp(AIfExp node, IRInfo question)
			throws AnalysisException
	{
		PExpCG testExp = node.getTest().apply(question.getExpVisitor(), question);
		PExpCG thenExp = node.getThen().apply(question.getExpVisitor(), question);
		PTypeCG expectedType = node.getType().apply(question.getTypeVisitor(), question);
		
		ATernaryIfExpCG ternaryIf = new ATernaryIfExpCG();
		
		ternaryIf.setCondition(testExp);
		ternaryIf.setTrueValue(thenExp);
		ternaryIf.setType(expectedType);
		
		LinkedList<AElseIfExp> elseExpList = node.getElseList();
		
		ATernaryIfExpCG nextTernaryIf = ternaryIf;
		
		for (AElseIfExp currentElseExp : elseExpList)
		{
			ATernaryIfExpCG tmp = new ATernaryIfExpCG();
			
			testExp = currentElseExp.getElseIf().apply(question.getExpVisitor(), question);
			thenExp = currentElseExp.getThen().apply(question.getExpVisitor(), question);
			expectedType = currentElseExp.getType().apply(question.getTypeVisitor(), question);
			
			tmp.setCondition(testExp);
			tmp.setTrueValue(thenExp);
			tmp.setType(expectedType);
			
			nextTernaryIf.setFalseValue(tmp);
			nextTernaryIf = tmp;
			
		}
		
		PExpCG elseExp = node.getElse().apply(question.getExpVisitor(), question);
		nextTernaryIf.setFalseValue(elseExp);
		
		if(node.parent() instanceof SBinaryExp)
			return question.getExpAssistant().isolateExpression(ternaryIf);
		
		return ternaryIf;
	}
	
	@Override
	public PExpCG caseATupleExp(ATupleExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		LinkedList<PExp> args = node.getArgs();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		ATupleExpCG tupleExp = new ATupleExpCG();
		tupleExp.setType(typeCg);
		
		for (PExp exp : args)
		{
			PExpCG expCg = exp.apply(question.getExpVisitor(), question);
			tupleExp.getArgs().add(expCg);
		}
		
		return tupleExp;
	}
	
	@Override
	public PExpCG caseAFieldNumberExp(AFieldNumberExp node, IRInfo question)
			throws AnalysisException
	{
		long fieldCg = node.getField().getValue();
		PType type = node.getType();
		PExp tuple = node.getTuple();

		AFieldNumberExpCG fieldNoExp = new AFieldNumberExpCG();
		PExpCG tupleCg = tuple.apply(question.getExpVisitor(), question);
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		fieldNoExp.setField(fieldCg);
		fieldNoExp.setType(typeCg);
		fieldNoExp.setTuple(tupleCg);
		
		return fieldNoExp;
	}
	
	@Override
	public PExpCG caseAFuncInstatiationExp(AFuncInstatiationExp node,
			IRInfo question) throws AnalysisException
	{
		String name = node.getExpdef().getName().getName();
		LinkedList<PType> actualTypes = node.getActualTypes();
		
		AMethodInstantiationExpCG methodInst = new AMethodInstantiationExpCG();
		methodInst.setType(null);
		methodInst.setName(name);
		
		for (PType type : actualTypes)
		{
			PTypeCG typeCG = type.apply(question.getTypeVisitor(), question);
			methodInst.getActualTypes().add(typeCG);
		}
		
		return methodInst;
	}
	
	@Override
	public PExpCG caseALetBeStExp(ALetBeStExp node, IRInfo question)
			throws AnalysisException
	{
		if (question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, "Generation of a let be st expression is only supported within operations/functions");
			return null;
		}
		
		PMultipleBind multipleBind = node.getBind();
		
		if(!(multipleBind instanceof ASetMultipleBind))
		{
			question.addUnsupportedNode(node, "Generation of the let be st expression is only supported for a multiple set bind. Got: " + multipleBind);
			return null;
		}
		
		ASetMultipleBind multipleSetBind = (ASetMultipleBind) multipleBind;
		
		PMultipleBindCG multipleBindCg = multipleSetBind.apply(question.getMultipleBindVisitor(), question);
		
		if(!(multipleBindCg instanceof ASetMultipleBindCG))
		{
			question.addUnsupportedNode(node, "Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: " + multipleBindCg);
			return null;
		}
		
		ASetMultipleBindCG multipleSetBindCg = (ASetMultipleBindCG) multipleBindCg;
		
		PType type = node.getType();
		PExp suchThat = node.getSuchThat();
		PExp value = node.getValue();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG suchThatCg = suchThat != null ? suchThat.apply(question.getExpVisitor(), question) : null;
		PExpCG valueCg = value.apply(question.getExpVisitor(), question);
		
		ALetBeStExpCG letBeStExp = new ALetBeStExpCG();
		
		AHeaderLetBeStCG header = question.getExpAssistant().consHeader(multipleSetBindCg, suchThatCg);

		letBeStExp.setType(typeCg);
		letBeStExp.setHeader(header);
		letBeStExp.setValue(valueCg);
		
		return letBeStExp;
	}
	
	@Override
	public PExpCG caseALetDefExp(ALetDefExp node, IRInfo question)
			throws AnalysisException
	{
		if(question.getExpAssistant().isAssigned(node))
		{
			question.addUnsupportedNode(node, "Generation of a let expression is not supported in assignments");
			return null;
		}
		
		ALetDefExpCG localDefExp = new ALetDefExpCG();
	
		question.getDeclAssistant().setLocalDefs(node.getLocalDefs(), localDefExp.getLocalDefs(), question);
		
		PExpCG exp = node.getExpression().apply(question.getExpVisitor(), question);
		localDefExp.setExp(exp);
		
		return localDefExp;
	}

	
	@Override
	public PExpCG caseAMkTypeExp(AMkTypeExp node, IRInfo question)
			throws AnalysisException
	{
		ARecordInvariantType recType = node.getRecordType();
		
		if(recType == null)
			throw new AnalysisExceptionCG("Expected record type for mk_<type> expression.", node.getLocation());
		
		PTypeCG typeCg = recType.apply(question.getTypeVisitor(), question);
		
		if(!(typeCg instanceof ARecordTypeCG))
			throw new AnalysisExceptionCG("Expected record type but got: " + typeCg.getClass().getName() + " in 'mk_' expression", node.getLocation());
		
		ARecordTypeCG recordTypeCg = (ARecordTypeCG) typeCg;
		
		LinkedList<PExp> nodeArgs = node.getArgs();
		
		ANewExpCG newExp = new ANewExpCG();
		newExp.setType(recordTypeCg);
		newExp.setName(recordTypeCg.getName().clone());

		LinkedList<PExpCG> newExpArgs = newExp.getArgs();
		
		for (PExp arg : nodeArgs)
		{
			newExpArgs.add(arg.apply(question.getExpVisitor(), question));
		}
		
		return newExp;
	}
	
	@Override
	public PExpCG caseASelfExp(ASelfExp node, IRInfo question)
			throws AnalysisException
	{
		return new ASelfExpCG();
	}
	
	@Override
	public PExpCG caseAReverseUnaryExp(AReverseUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		PType type = node.getType();

		if (!(type instanceof SSeqType))
			throw new AnalysisExceptionCG("Unexpected sequence type for reverse unary expression: "
					+ type.getClass().getName(), node.getLocation());

		SSeqType seqType = ((SSeqType) type);

		PTypeCG seqTypeCg = seqType.apply(question.getTypeVisitor(), question);
		PExpCG expCg = exp.apply(question.getExpVisitor(), question);

		AReverseUnaryExpCG reverse = new AReverseUnaryExpCG();
		reverse.setExp(expCg);
		reverse.setType(seqTypeCg);

		return reverse;
	}
	
	@Override
	public PExpCG caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistConcatUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASeqCompSeqExp(ASeqCompSeqExp node, IRInfo question)
			throws AnalysisException
	{
		if (question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, "Generation of a sequence comprehension is only supported within operations/functions");
			return null;
		}
		
		PPattern pattern = node.getSetBind().getPattern();
		
		if(!(pattern instanceof AIdentifierPattern))
		{
			question.addUnsupportedNode(node, "Generation of a sequence comprehension is only supported for identifier patterns");
			return null;
		}
		
		AIdentifierPattern setBindId = (AIdentifierPattern) pattern;
		PType type = node.getType();
		PExp first = node.getFirst();
		PExp set = node.getSetBind().getSet();
		PExp predicate = node.getPredicate();

		AIdentifierPatternCG id = new AIdentifierPatternCG();
		id.setName(setBindId.getName().getName());
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG firstCg = first.apply(question.getExpVisitor(), question);
		PExpCG setCg = set.apply(question.getExpVisitor(), question);
		PExpCG predicateCg = predicate != null ? predicate.apply(question.getExpVisitor(), question) : null;
		
		ACompSeqExpCG seqComp = new ACompSeqExpCG();
		seqComp.setId(id);
		seqComp.setType(typeCg);
		seqComp.setFirst(firstCg);
		seqComp.setSet(setCg);
		seqComp.setPredicate(predicateCg);
		
		return seqComp;
	}
	
	@Override
	public PExpCG caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node,  new ASeqConcatBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		PType leftType = node.getLeft().getType();
		
		if(leftType instanceof SSeqType)
			return question.getExpAssistant().handleBinaryExp(node, new ASeqModificationBinaryExpCG(), question);
		else if(leftType instanceof SMapType)
			return question.getExpAssistant().handleBinaryExp(node, new AMapOverrideBinaryExpCG(), question);
					
		throw new AnalysisExceptionCG("Expected sequence or map type for '++' binary expression but got: " + leftType, node.getLocation());			
	}
	
	@Override
	public PExpCG caseAMapEnumMapExp(AMapEnumMapExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		AEnumMapExpCG enumMap = new AEnumMapExpCG();
		enumMap.setType(typeCg);
		
		LinkedList<AMapletExp> members = node.getMembers();
		for (PExp member : members)
		{
			PExpCG exp = member.apply(question.getExpVisitor(), question);
			
			if(!(exp instanceof AMapletExpCG))
				throw new AnalysisExceptionCG("Got expected map enumeration member: " + exp, member.getLocation());
			
			enumMap.getMembers().add((AMapletExpCG) exp);
		}
		
		return enumMap;
	}
	
	@Override
	public PExpCG caseAMapletExp(AMapletExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		PExp left = node.getLeft();
		PExp right = node.getRight();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		PExpCG leftCg = left.apply(question.getExpVisitor(), question);
		PExpCG rightCg = right.apply(question.getExpVisitor(), question);
		
		AMapletExpCG maplet = new AMapletExpCG();
		maplet.setType(typeCg);
		maplet.setLeft(leftCg);
		maplet.setRight(rightCg);
		
		return maplet;
	}
	
	@Override
	public PExpCG caseAMapCompMapExp(AMapCompMapExp node, IRInfo question)
			throws AnalysisException
	{
		if (question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, "Generation of a map comprehension is only supported within operations/functions");
			return null;
		}
		
		LinkedList<PMultipleBind> bindings = node.getBindings();
		PType type = node.getType();
		AMapletExp first = node.getFirst();
		PExp predicate = node.getPredicate();
		
		LinkedList<ASetMultipleBindCG> bindingsCg = new LinkedList<ASetMultipleBindCG>();
		for (PMultipleBind multipleBind : bindings)
		{
			if(!(multipleBind instanceof ASetMultipleBind))
			{
				question.addUnsupportedNode(node, "Generation of a map comprehension is only supported for multiple set binds. Got: "
						+ multipleBind);
				return null;
			}
			
			PMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
			
			if (!(multipleBindCg instanceof ASetMultipleBindCG))
			{
				question.addUnsupportedNode(node, "Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: " + multipleBindCg);
			}
			
			bindingsCg.add((ASetMultipleBindCG) multipleBindCg);
		}
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG firstCg = first.apply(question.getExpVisitor(), question);
		PExpCG predicateCg = predicate != null ? predicate.apply(question.getExpVisitor(), question) : null;
		
		if(!(firstCg instanceof AMapletExpCG))
		{
			question.addUnsupportedNode(node, "Generation of map comprehension expected a maplet expression. Got: " + firstCg);
		}
		
		AMapletExpCG mapletExpCg = (AMapletExpCG) firstCg;
		
		ACompMapExpCG mapComp = new ACompMapExpCG();
		
		mapComp.setBindings(bindingsCg);
		mapComp.setType(typeCg);
		mapComp.setFirst(mapletExpCg);
		mapComp.setPredicate(predicateCg);

		return mapComp;
	}
	
	@Override
	public PExpCG caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapDomainUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapRangeUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AMapUnionBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistMergeUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ADomainResToBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ADomainResByBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ARangeResToBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ARangeResByBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAMapInverseUnaryExp(AMapInverseUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapInverseUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAEqualsBinaryExp(AEqualsBinaryExp node,
			IRInfo question) throws AnalysisException
	{	
		return question.getExpAssistant().handleBinaryExp(node, new AEqualsBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ANotEqualsBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAIndicesUnaryExp(AIndicesUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AIndicesUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASeqEnumSeqExp(ASeqEnumSeqExp node, IRInfo question)
			throws AnalysisException
	{	
		PType type = node.getType();

		AEnumSeqExpCG enumSeq = new AEnumSeqExpCG();
		
		if(type instanceof SSeqType)
		{
			PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
			enumSeq.setType(typeCg);
		}
		else
		{
			question.addUnsupportedNode(node, "Unexpected sequence type for sequence enumeration expression: " + type.getClass().getName());
			return null;
		}
		
		LinkedList<PExp> members = node.getMembers();
		for (PExp member : members)
		{
			PExpCG memberCg = member.apply(question.getExpVisitor(), question);
			enumSeq.getMembers().add(memberCg);
		}
		
		return enumSeq;
	}
	
	@Override
	public PExpCG caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, IRInfo question)
			throws AnalysisException
	{
		return null;//Indicates an abstract body
	}
	
	@Override
	public PExpCG caseAFieldExp(AFieldExp node, IRInfo question)
			throws AnalysisException
	{
		PExpCG object = node.getObject().apply(question.getExpVisitor(), question);
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		
		String memberName = "";
		
		if(node.getMemberName() != null)
			memberName = node.getMemberName().getFullName();
		else
			memberName = node.getField().getName();
		
		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setObject(object);
		fieldExp.setMemberName(memberName);
		fieldExp.setType(type);
		
		return fieldExp;
	}
	
	@Override
	public PExpCG caseAApplyExp(AApplyExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp root = node.getRoot();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG rootCg = root.apply(question.getExpVisitor(), question);

		AApplyExpCG applyExp = new AApplyExpCG();
		applyExp.setType(typeCg);
		applyExp.setRoot(rootCg);

		for (PExp arg : node.getArgs())
		{
			PExpCG argCg = arg.apply(question.getExpVisitor(), question);
			
			if(argCg == null)
			{
				question.addUnsupportedNode(node, "Apply expression is not supported for the argument: " + arg);
				return null;
			}
			
			applyExp.getArgs().add(argCg);
		}

		return applyExp;
	}
	
	@Override
	public PExpCG caseAVariableExp(AVariableExp node, IRInfo question)
			throws AnalysisException
	{
		PDefinition varDef = node.getVardef();
		PType type = node.getType();
		String name = node.getName().getName();
		
		SClassDefinition owningClass = varDef.getAncestor(SClassDefinition.class);
		SClassDefinition nodeParentClass = node.getAncestor(SClassDefinition.class);

		boolean inOwningClass = owningClass == nodeParentClass;
		
		boolean isLocalDef = varDef instanceof ALocalDefinition;
		boolean isInstanceVarDef = varDef instanceof AInstanceVariableDefinition;
		boolean isExplOp = varDef instanceof SOperationDefinition;
		boolean isExplFunc = varDef instanceof SFunctionDefinition;
		boolean isAssignmentDef = varDef instanceof AAssignmentDefinition;
		
		boolean isDefInOwningClass = inOwningClass && (isLocalDef || isInstanceVarDef || isExplOp || isExplFunc || isAssignmentDef);

		boolean isImplicit = !node.getName().getExplicit();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		PTypeAssistantTC typeAssistant = question.getTcFactory().createPTypeAssistant();

		boolean isLambda = typeAssistant.isFunction(type) && !(varDef instanceof SFunctionDefinition);

		if (owningClass == null || nodeParentClass == null || isDefInOwningClass || isImplicit)
		{
			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();
			
			varExp.setType(typeCg);
			varExp.setOriginal(name);
			varExp.setIsLambda(isLambda);

			return varExp;
		}
		else if(node.getName().getExplicit())
		{
			AExplicitVarExpCG varExp = new AExplicitVarExpCG();
			
			String className = node.getName().getModule();
			
			AClassTypeCG classType = new AClassTypeCG();
			classType.setName(className);

			varExp.setType(typeCg);
			varExp.setClassType(classType);
			varExp.setName(name);
			varExp.setIsLambda(isLambda);
			
			return varExp;
		}
		else
			return null; 
	}
	
	@Override
	public PExpCG caseANewExp(ANewExp node, IRInfo question)
			throws AnalysisException
	{
		String className = node.getClassdef().getName().getName();
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(null);
		typeName.setName(className);
		
		PType type = node.getType();
		LinkedList<PExp> nodeArgs = node.getArgs();

		ANewExpCG newExp = new ANewExpCG();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		newExp.setType(typeCg);
		newExp.setName(typeName);
		
		LinkedList<PExpCG> newExpArgs = newExp.getArgs();
		for (PExp arg : nodeArgs)
		{
			newExpArgs.add(arg.apply(question.getExpVisitor(), question));
		}
		
		return newExp;
	}
		
	@Override
	public PExpCG caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ATimesNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new APlusNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASubtractNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AGreaterEqualNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAStarStarBinaryExp(AStarStarBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new APowerNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AGreaterNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ALessEqualNumericBinaryExpCG(), question);
	}
	
	
	@Override
	public PExpCG caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ALessNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		ADivideNumericBinaryExpCG divide = (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		PExpCG leftExpCG = divide.getLeft();
		
		if(question.getExpAssistant().isIntegerType(leftExp) && question.getExpAssistant().isIntegerType(rightExp))
		{
			ARealLiteralExpCG one = new ARealLiteralExpCG();
			one.setType(new ARealNumericBasicTypeCG());
			one.setValue(1.0);
			
			ATimesNumericBinaryExpCG neutralMul = new ATimesNumericBinaryExpCG();
			neutralMul.setType(new ARealNumericBasicTypeCG());
			neutralMul.setLeft(one);
			neutralMul.setRight(leftExpCG);
			
			divide.setLeft(question.getExpAssistant().isolateExpression(neutralMul));
		}
		
		return divide;
	}
	
	@Override
	public PExpCG caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		//VDM Language Reference Manual:
		//x mod y = x - y * floor(x/y)
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		ADivideNumericBinaryExpCG div = (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		AFloorUnaryExpCG floor = new AFloorUnaryExpCG();
		floor.setExp(div);
		
		PExpCG leftExpCg = leftExp.apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = rightExp.apply(question.getExpVisitor(), question);
		
		ATimesNumericBinaryExpCG times = new ATimesNumericBinaryExpCG();
		times.setLeft(rightExpCg);
		times.setRight(floor);
		
		ASubtractNumericBinaryExpCG sub = new ASubtractNumericBinaryExpCG();
		sub.setLeft(leftExpCg);
		sub.setRight(times);
		
		return (node.parent() instanceof SBinaryExp) ? question.getExpAssistant().isolateExpression(sub) : sub;
	}
	
	@Override
	public PExpCG caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		//VDM Language Reference Manual:
		//x rem y = x - y * (x div y)
		
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();
		
		ADivideNumericBinaryExpCG div = (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		
		PExpCG leftExpCg = leftExp.apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = rightExp.apply(question.getExpVisitor(), question);
		
		ATimesNumericBinaryExpCG times = new ATimesNumericBinaryExpCG();
		times.setLeft(rightExpCg);
		times.setRight(question.getExpAssistant().isolateExpression(div));
		
		ASubtractNumericBinaryExpCG sub = new ASubtractNumericBinaryExpCG();
		sub.setLeft(leftExpCg);
		sub.setRight(times);
		
		return (node.parent() instanceof SBinaryExp) ? question.getExpAssistant().isolateExpression(sub) : sub;
	}
				
	@Override
	public PExpCG caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		//A => B is constructed as !A || B
		
		PTypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = node.getRight().apply(question.getExpVisitor(), question);
		
		ANotUnaryExpCG notExp = new ANotUnaryExpCG();
		notExp.setType(typeCg);
		notExp.setExp(leftExpCg);
		
		AOrBoolBinaryExpCG orExp = new AOrBoolBinaryExpCG();
		orExp.setType(typeCg);
		orExp.setLeft(notExp);
		orExp.setRight(rightExpCg);
		
		return orExp;
	}
	
	@Override
	public PExpCG caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		//A <=> B is constructed as !(A ^ B)
		PTypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = node.getRight().apply(question.getExpVisitor(), question);

		AXorBoolBinaryExpCG xorExp = new AXorBoolBinaryExpCG();
		xorExp.setType(typeCg);
		xorExp.setLeft(leftExpCg);
		xorExp.setRight(rightExpCg);
		
		ANotUnaryExpCG notExp = new ANotUnaryExpCG();
		notExp.setType(typeCg);
		notExp.setExp(question.getExpAssistant().isolateExpression(xorExp));

		return notExp;
	}
	
	//Unary
	
	@Override
	public PExpCG caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node, IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new APlusUnaryExpCG(), question);
	}

	@Override
	public PExpCG caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMinusUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAFloorUnaryExp(AFloorUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AFloorUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AAbsUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseANotUnaryExp(ANotUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ANotUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node,  new AOrBoolBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node,  new AAndBoolBinaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseALenUnaryExp(ALenUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ASizeUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAElementsUnaryExp(AElementsUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AElemsUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseAHeadUnaryExp(AHeadUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AHeadUnaryExpCG(), question);
	}
	
	@Override
	public PExpCG caseATailUnaryExp(ATailUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ATailUnaryExpCG(), question);
	}
	
	//Literals
	//NOTE: The methods for handling of literals/constants look very similar and ideally should be
	//generalized in a method. However the nodes in the VDM AST don't share a parent with method
	//setValue at the current time of writing.
	
	@Override
	public PExpCG caseABooleanConstExp(ABooleanConstExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().consBoolLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseARealLiteralExp(ARealLiteralExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().consRealLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseAIntLiteralExp(AIntLiteralExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().consIntLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseACharLiteralExp(ACharLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().consCharLiteral(node.getValue().getValue());
	}
	
	@Override
	public PExpCG caseAStringLiteralExp(AStringLiteralExp node,
			IRInfo question) throws AnalysisException
	{
		if (question.getSettings().getCharSeqAsString())
		{
			return question.getExpAssistant().consStringLiteral(node.getValue().getValue(), false);
		} else
		{
			AEnumSeqExpCG enumSeq = new AEnumSeqExpCG();

			ASeqSeqTypeCG seqType = new ASeqSeqTypeCG();
			seqType.setEmpty(node.getValue().getValue().isEmpty());
			seqType.setSeqOf(new ACharBasicTypeCG());

			enumSeq.setType(seqType);

			String str = node.getValue().getValue();

			for (int i = 0; i < str.length(); i++)
			{
				char currentChar = str.charAt(i);
				ACharLiteralExpCG charLit = new ACharLiteralExpCG();
				charLit.setType(new ACharBasicTypeCG());
				charLit.setValue(currentChar);

				enumSeq.getMembers().add(charLit);
			}

			return enumSeq;
		}
	}
	
	@Override
	public PExpCG caseAQuoteLiteralExp(AQuoteLiteralExp node,
			IRInfo question) throws AnalysisException
	{
		String value = node.getValue().getValue();
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);

		AQuoteLiteralExpCG quoteLit = new AQuoteLiteralExpCG();
		quoteLit.setValue(value);
		quoteLit.setType(type);

		question.registerQuoteValue(value);
		
		return quoteLit;
	}
	
	@Override
	public PExpCG caseALambdaExp(ALambdaExp node, IRInfo question)
			throws AnalysisException
	{
		LinkedList<ATypeBind> bindList = node.getBindList();
		PExp exp = node.getExpression();
		PType type = node.getType();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PExpCG expCg = exp.apply(question.getExpVisitor(), question);		
		
		ALambdaExpCG lambdaExp = new ALambdaExpCG();
		
		lambdaExp.setType(typeCg);
		lambdaExp.setExp(expCg);
		
		LinkedList<AFormalParamLocalDeclCG> params = lambdaExp.getParams();
		
		for(ATypeBind typeBind : bindList)
		{
			PPattern bindPattern = typeBind.getPattern();
			
			if(!(bindPattern instanceof AIdentifierPattern))
			{
				question.addUnsupportedNode(node, "Expected identifier pattern for lambda expression. Got: " + bindPattern);
				return null;
			}
			
			AIdentifierPattern idPattern = (AIdentifierPattern) bindPattern;
			PType bindType = typeBind.getType();
			

			String name = idPattern.getName().getName();
			PTypeCG bindTypeCg = bindType.apply(question.getTypeVisitor(), question);
			
			AFormalParamLocalDeclCG param = new AFormalParamLocalDeclCG();
			param.setName(name);
			param.setType(bindTypeCg);
			
			params.add(param);
		}
		
		
		return lambdaExp;
	}
}
