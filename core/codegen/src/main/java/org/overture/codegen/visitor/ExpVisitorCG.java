/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
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
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
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
import org.overture.ast.expressions.AIsExp;
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
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.APreOpExp;
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
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.AThreadIdExp;
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
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.cgast.SBindCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.expressions.AAbsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACaseAltExpExpCG;
import org.overture.codegen.cgast.expressions.ACasesExpCG;
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
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ALambdaExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
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
import org.overture.codegen.cgast.expressions.ANotImplementedExpCG;
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
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.ASubSeqExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATailUnaryExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.AThreadIdExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.expressions.ATupleIsExpCG;
import org.overture.codegen.cgast.expressions.AXorBoolBinaryExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.ASetBindCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class ExpVisitorCG extends AbstractVisitorCG<IRInfo, SExpCG>
{
	@Override
	public SExpCG caseAPreOpExp(APreOpExp node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExpression();
		
		return exp.apply(question.getExpVisitor(), question);
	}
	
	@Override
	public SExpCG caseAPostOpExp(APostOpExp node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getPostexpression();
		
		return exp.apply(question.getExpVisitor(), question);
	}
	
	@Override
	public SExpCG caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			IRInfo question) throws AnalysisException
	{
		return new ANotImplementedExpCG();
	}

	@Override
	public SExpCG caseAThreadIdExp(AThreadIdExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		STypeCG typeCG = type.apply(question.getTypeVisitor(), question);

		AThreadIdExpCG threadId = new AThreadIdExpCG();
		threadId.setType(typeCG);

		return threadId;

	}

	@Override
	public SExpCG caseANilExp(ANilExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		ANullExpCG nullExpCg = new ANullExpCG();
		nullExpCg.setType(typeCg);

		return nullExpCg;
	}

	@Override
	public SExpCG caseAMkBasicExp(AMkBasicExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		if (!(type instanceof ATokenBasicType))
		{
			question.addUnsupportedNode(node, "Expected token type for mk basic expression. Got: "
					+ type);
			return null;
		}

		PExp arg = node.getArg();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG argCg = arg.apply(question.getExpVisitor(), question);

		AMkBasicExpCG mkBasicExp = new AMkBasicExpCG();
		mkBasicExp.setType(typeCg);
		mkBasicExp.setArg(argCg);

		return mkBasicExp;
	}
	
	@Override
	public SExpCG caseAIsExp(AIsExp node, IRInfo question)
			throws AnalysisException
	{
		String reason = "The 'is' expression is currently only supported for basic basic types, records, tuples and classes";
		
		PType type = node.getType();
		PType checkedType = node.getBasicType();
		PExp exp = node.getTest();

		if(checkedType != null && question.getTcFactory().createPTypeAssistant().isUnion(checkedType))
		{
			question.addUnsupportedNode(node, reason);
			return null;
		}
		
		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);
		
		if (checkedType == null)
		{
			checkedType = question.getTcFactory().createPDefinitionAssistant().getType(node.getTypedef());
			
			if(question.getTcFactory().createPTypeAssistant().isUnion(checkedType))
			{
				question.addUnsupportedNode(node, reason);
				return null;
			}

			if(question.getTcFactory().createPTypeAssistant().isProduct(checkedType))
			{
				AProductType productType = question.getTcFactory().createPTypeAssistant().getProduct(checkedType);
				STypeCG checkedTypeCg = productType.apply(question.getTypeVisitor(), question);
				
				ATupleIsExpCG tupleIsExp = new ATupleIsExpCG();
				tupleIsExp.setType(typeCg);
				tupleIsExp.setExp(expCg);
				tupleIsExp.setCheckedType(checkedTypeCg);
				
				return tupleIsExp;
			}
			else
			{
				if(question.getTcFactory().createPTypeAssistant().isRecord(checkedType))
				{
					checkedType = question.getTcFactory().createPTypeAssistant().getRecord(checkedType);
				}
				else if(question.getTcFactory().createPTypeAssistant().isClass(checkedType))
				{
					checkedType = question.getTcFactory().createPTypeAssistant().getClassType(checkedType);
				}
				else
				{
					checkedType = null;
				}
				
				if (checkedType != null)
				{
					STypeCG checkedTypeCg = checkedType.apply(question.getTypeVisitor(), question);

					return question.getExpAssistant().consGeneralIsExp(typeCg, expCg, checkedTypeCg);
				} else
				{
					question.addUnsupportedNode(node, reason);
					return null;
				}
			}
		} else
		{
			if (checkedType instanceof SBasicType)
			{
				return question.getExpAssistant().consIsExpBasicType(node, question, reason, checkedType, typeCg, expCg);
			} else if(checkedType instanceof AQuoteType)
			{
				return question.getExpAssistant().consIsExpQuoteType(question, checkedType, expCg);
			}
			else
			{
				question.addUnsupportedNode(node, reason);
				return null;
			}
		}
	}

	@Override
	public SExpCG caseAIsOfClassExp(AIsOfClassExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		AClassType classType = node.getClassType();
		PExp objRef = node.getExp();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		STypeCG classTypeCg = classType.apply(question.getTypeVisitor(), question);

		if (!(classTypeCg instanceof AClassTypeCG))
		{
			Logger.getLog().printErrorln("Unexpected class type encountered for "
					+ AIsOfClassExp.class.getName()
					+ ". Expected class type: "
					+ AClassTypeCG.class.getName()
					+ ". Got: "
					+ typeCg.getClass().getName() + " at " +  node.getLocation());
		}

		SExpCG objRefCg = objRef.apply(question.getExpVisitor(), question);

		AInstanceofExpCG instanceOfExp = new AInstanceofExpCG();
		instanceOfExp.setType(typeCg);
		instanceOfExp.setCheckedType(classTypeCg);
		instanceOfExp.setExp(objRefCg);

		return instanceOfExp;
	}

	@Override
	public SExpCG caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ASizeUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAInSetBinaryExp(AInSetBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AInSetBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().negate(question.getExpAssistant().handleBinaryExp(node, new AInSetBinaryExpCG(), question));
	}

	@Override
	public SExpCG caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetUnionBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetIntersectBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetDifferenceBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseASubsetBinaryExp(ASubsetBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetSubsetBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetProperSubsetBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistUnionUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistIntersectUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAPowerSetUnaryExp(APowerSetUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new APowerSetUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseASetEnumSetExp(ASetEnumSetExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		if (!(type instanceof ASetType))
		{
			Logger.getLog().printErrorln("Unexpected set type for set enumeration expression: "
					+ type.getClass().getName() + " at "  + node.getLocation());
		}

		LinkedList<PExp> members = node.getMembers();

		AEnumSetExpCG enumSet = new AEnumSetExpCG();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		enumSet.setType(typeCg);
		LinkedList<SExpCG> membersCg = enumSet.getMembers();

		for (PExp member : members)
		{
			membersCg.add(member.apply(question.getExpVisitor(), question));
		}

		return enumSet;
	}

	@Override
	public SExpCG caseAForAllExp(AForAllExp node, IRInfo question)
			throws AnalysisException
	{
		// The inheritance hierarchy of the VDM AST tree is structured such that the bindings and the predicate
		// must also be passed to the method that handles the forall and the exists quantifiers
		return question.getExpAssistant().handleQuantifier(node, node.getBindList(), node.getPredicate(), new AForAllQuantifierExpCG(), question, "forall expression");
	}

	@Override
	public SExpCG caseAExistsExp(AExistsExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleQuantifier(node, node.getBindList(), node.getPredicate(), new AExistsQuantifierExpCG(), question, "exists expression");
	}

	@Override
	public SExpCG caseAExists1Exp(AExists1Exp node, IRInfo question)
			throws AnalysisException
	{
		PBind bind = node.getBind();

		if (!(bind instanceof ASetBind))
		{
			question.addUnsupportedNode(node, String.format("Generation of a exist1 expression is only supported for set binds. Got: %s", bind));
			return null;
		}

		SBindCG bindCg = bind.apply(question.getBindVisitor(), question);

		if (!(bindCg instanceof ASetBindCG))
		{
			question.addUnsupportedNode(node, String.format("Generation of 	a set bind was expected to yield a ASetBindCG. Got: %s", bindCg));
			return null;
		}

		ASetBindCG setBind = (ASetBindCG) bindCg;

		PType type = node.getType();
		PExp predicate = node.getPredicate();

		ASetMultipleBindCG multipleSetBind = question.getBindAssistant().convertToMultipleSetBind(setBind);
		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG predicateCg = predicate.apply(question.getExpVisitor(), question);

		AExists1QuantifierExpCG exists1Exp = new AExists1QuantifierExpCG();
		exists1Exp.getBindList().add(multipleSetBind);
		exists1Exp.setType(typeCg);
		exists1Exp.setPredicate(predicateCg);

		return exists1Exp;
	}

	@Override
	public SExpCG caseASetCompSetExp(ASetCompSetExp node, IRInfo question)
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
			if (!(multipleBind instanceof ASetMultipleBind))
			{
				question.addUnsupportedNode(node, "Generation of a set comprehension is only supported for multiple set binds. Got: "
						+ multipleBind);
				return null;
			}

			SMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);

			if (!(multipleBindCg instanceof ASetMultipleBindCG))
			{
				question.addUnsupportedNode(node, "Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: "
						+ multipleBindCg);
			}

			bindingsCg.add((ASetMultipleBindCG) multipleBindCg);
		}

		PType type = node.getType();
		PExp first = node.getFirst();
		PExp predicate = node.getPredicate();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG firstCg = first.apply(question.getExpVisitor(), question);
		SExpCG predicateCg = predicate != null ? predicate.apply(question.getExpVisitor(), question)
				: null;

		ACompSetExpCG setComp = new ACompSetExpCG();
		setComp.setBindings(bindingsCg);
		setComp.setType(typeCg);
		setComp.setFirst(firstCg);
		setComp.setPredicate(predicateCg);

		return setComp;
	}

	@Override
	public SExpCG caseASetRangeSetExp(ASetRangeSetExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp firstExp = node.getFirst();
		PExp lastExp = node.getLast();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG firstExpCg = firstExp.apply(question.getExpVisitor(), question);
		SExpCG lastExpCg = lastExp.apply(question.getExpVisitor(), question);

		ARangeSetExpCG setRange = new ARangeSetExpCG();

		setRange.setType(typeCg);
		setRange.setFirst(firstExpCg);
		setRange.setLast(lastExpCg);

		return setRange;
	}

	@Override
	public SExpCG caseACasesExp(ACasesExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp exp = node.getExpression();
		PExp others = node.getOthers();
		LinkedList<ACaseAlternative> cases = node.getCases();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);
		SExpCG othersCg = others != null ? others.apply(question.getExpVisitor(), question)
				: null;

		ACasesExpCG casesExpCg = new ACasesExpCG();
		casesExpCg.setType(typeCg);
		casesExpCg.setExp(expCg);
		casesExpCg.setOthers(othersCg);

		question.getExpAssistant().handleAlternativesCasesExp(question, exp, cases, casesExpCg.getCases());

		return casesExpCg;
	}

	@Override
	public SExpCG caseACaseAlternative(ACaseAlternative node, IRInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		PExp result = node.getResult();

		SPatternCG patternCg = pattern.apply(question.getPatternVisitor(), question);
		SExpCG resultCg = result.apply(question.getExpVisitor(), question);

		ACaseAltExpExpCG altCg = new ACaseAltExpExpCG();

		altCg.setPattern(patternCg);
		altCg.setResult(resultCg);

		return altCg;
	}

	@Override
	public SExpCG caseAIfExp(AIfExp node, IRInfo question)
			throws AnalysisException
	{
		SExpCG testExp = node.getTest().apply(question.getExpVisitor(), question);
		SExpCG thenExp = node.getThen().apply(question.getExpVisitor(), question);
		STypeCG expectedType = node.getType().apply(question.getTypeVisitor(), question);

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

		SExpCG elseExp = node.getElse().apply(question.getExpVisitor(), question);
		nextTernaryIf.setFalseValue(elseExp);

		if (node.parent() instanceof SBinaryExp)
		{
			return question.getExpAssistant().isolateExpression(ternaryIf);
		}

		return ternaryIf;
	}

	@Override
	public SExpCG caseATupleExp(ATupleExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		LinkedList<PExp> args = node.getArgs();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		ATupleExpCG tupleExp = new ATupleExpCG();
		tupleExp.setType(typeCg);

		for (PExp exp : args)
		{
			SExpCG expCg = exp.apply(question.getExpVisitor(), question);
			tupleExp.getArgs().add(expCg);
		}

		return tupleExp;
	}

	@Override
	public SExpCG caseAFieldNumberExp(AFieldNumberExp node, IRInfo question)
			throws AnalysisException
	{
		long fieldCg = node.getField().getValue();
		PType type = node.getType();
		PExp tuple = node.getTuple();

		AFieldNumberExpCG fieldNoExp = new AFieldNumberExpCG();
		SExpCG tupleCg = tuple.apply(question.getExpVisitor(), question);
		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		fieldNoExp.setField(fieldCg);
		fieldNoExp.setType(typeCg);
		fieldNoExp.setTuple(tupleCg);

		return fieldNoExp;
	}

	@Override
	public SExpCG caseAFuncInstatiationExp(AFuncInstatiationExp node,
			IRInfo question) throws AnalysisException
	{
		PType type = node.getType();
		PExp func = node.getFunction();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG funcCg = func.apply(question.getExpVisitor(), question);

		AMethodInstantiationExpCG methodInst = new AMethodInstantiationExpCG();

		LinkedList<PType> actualTypes = node.getActualTypes();
		for (PType actualType : actualTypes)
		{
			STypeCG actualTypeCg = actualType.apply(question.getTypeVisitor(), question);
			methodInst.getActualTypes().add(actualTypeCg);
		}

		methodInst.setFunc(funcCg);
		methodInst.setType(typeCg);

		return methodInst;
	}

	@Override
	public SExpCG caseALetBeStExp(ALetBeStExp node, IRInfo question)
			throws AnalysisException
	{
		if (question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, "Generation of a let be st expression is only supported within operations/functions");
			return null;
		}

		PMultipleBind multipleBind = node.getBind();

		if (!(multipleBind instanceof ASetMultipleBind))
		{
			question.addUnsupportedNode(node, "Generation of the let be st expression is only supported for a multiple set bind. Got: "
					+ multipleBind);
			return null;
		}

		ASetMultipleBind multipleSetBind = (ASetMultipleBind) multipleBind;

		SMultipleBindCG multipleBindCg = multipleSetBind.apply(question.getMultipleBindVisitor(), question);

		if (!(multipleBindCg instanceof ASetMultipleBindCG))
		{
			question.addUnsupportedNode(node, "Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: "
					+ multipleBindCg);
			return null;
		}

		ASetMultipleBindCG multipleSetBindCg = (ASetMultipleBindCG) multipleBindCg;

		PType type = node.getType();
		PExp suchThat = node.getSuchThat();
		PExp value = node.getValue();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG suchThatCg = suchThat != null ? suchThat.apply(question.getExpVisitor(), question)
				: null;
		SExpCG valueCg = value.apply(question.getExpVisitor(), question);

		ALetBeStExpCG letBeStExp = new ALetBeStExpCG();

		AHeaderLetBeStCG header = question.getExpAssistant().consHeader(multipleSetBindCg, suchThatCg);

		letBeStExp.setType(typeCg);
		letBeStExp.setHeader(header);
		letBeStExp.setValue(valueCg);

		return letBeStExp;
	}

	@Override
	public SExpCG caseALetDefExp(ALetDefExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().consLetDefExp(node, node.getLocalDefs(), node.getExpression(), node.getType(), question, "Generation of a let expression is not supported in assignments");
	}

	@Override
	public SExpCG caseAMkTypeExp(AMkTypeExp node, IRInfo question)
			throws AnalysisException
	{
		ARecordInvariantType recType = node.getRecordType();

		if (recType == null)
		{
			question.addUnsupportedNode(node, "Expected record type for mk_<type> expression. Got null.");
			return null;
		}

		STypeCG typeCg = recType.apply(question.getTypeVisitor(), question);

		if (!(typeCg instanceof ARecordTypeCG))
		{
			question.addUnsupportedNode(node, "Expected record type but got: "
					+ typeCg.getClass().getName() + " in 'mk_' expression");
			return null;
		}

		ARecordTypeCG recordTypeCg = (ARecordTypeCG) typeCg;

		LinkedList<PExp> nodeArgs = node.getArgs();

		ANewExpCG newExp = new ANewExpCG();
		newExp.setType(recordTypeCg);
		newExp.setName(recordTypeCg.getName().clone());

		LinkedList<SExpCG> newExpArgs = newExp.getArgs();

		for (PExp arg : nodeArgs)
		{
			newExpArgs.add(arg.apply(question.getExpVisitor(), question));
		}

		return newExp;
	}

	@Override
	public SExpCG caseASelfExp(ASelfExp node, IRInfo question)
			throws AnalysisException
	{
		return new ASelfExpCG();
	}

	@Override
	public SExpCG caseASubseqExp(ASubseqExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp from = node.getFrom();
		PExp to = node.getTo();
		PExp seq = node.getSeq();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG fromCg = from.apply(question.getExpVisitor(), question);
		SExpCG toCg = to.apply(question.getExpVisitor(), question);
		SExpCG seqCg = seq.apply(question.getExpVisitor(), question);

		ASubSeqExpCG subSeq = new ASubSeqExpCG();
		subSeq.setType(typeCg);
		subSeq.setFrom(fromCg);
		subSeq.setTo(toCg);
		subSeq.setSeq(seqCg);

		return subSeq;
	}

	@Override
	public SExpCG caseAReverseUnaryExp(AReverseUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		PType type = node.getType();

		if (!(type instanceof SSeqType))
		{
			question.addUnsupportedNode(node, "Unexpected sequence type for reverse unary expression: "
					+ type.getClass().getName());
			return null;
		}

		SSeqType seqType = (SSeqType) type;

		STypeCG seqTypeCg = seqType.apply(question.getTypeVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);

		AReverseUnaryExpCG reverse = new AReverseUnaryExpCG();
		reverse.setExp(expCg);
		reverse.setType(seqTypeCg);

		return reverse;
	}

	@Override
	public SExpCG caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistConcatUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseASeqCompSeqExp(ASeqCompSeqExp node, IRInfo question)
			throws AnalysisException
	{
		if (question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, "Generation of a sequence comprehension is only supported within operations/functions");
			return null;
		}

		ASetBind setBind = node.getSetBind();
		PType type = node.getType();
		PExp first = node.getFirst();
		PExp set = node.getSetBind().getSet();
		PExp predicate = node.getPredicate();

		SBindCG bindTempCg = setBind.apply(question.getBindVisitor(), question);

		if (!(bindTempCg instanceof ASetBindCG))
		{
			question.addUnsupportedNode(node, "Expected set bind for sequence comprehension. Got: "
					+ bindTempCg);
			return null;
		}

		ASetBindCG setBindCg = (ASetBindCG) bindTempCg;
		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG firstCg = first.apply(question.getExpVisitor(), question);
		SExpCG setCg = set.apply(question.getExpVisitor(), question);
		SExpCG predicateCg = predicate != null ? predicate.apply(question.getExpVisitor(), question)
				: null;

		ACompSeqExpCG seqComp = new ACompSeqExpCG();
		seqComp.setSetBind(setBindCg);
		seqComp.setType(typeCg);
		seqComp.setFirst(firstCg);
		seqComp.setSet(setCg);
		seqComp.setPredicate(predicateCg);

		return seqComp;
	}

	@Override
	public SExpCG caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASeqConcatBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		PType leftType = node.getLeft().getType();

		if (leftType instanceof SSeqType)
		{
			return question.getExpAssistant().handleBinaryExp(node, new ASeqModificationBinaryExpCG(), question);
		} else if (leftType instanceof SMapType)
		{
			return question.getExpAssistant().handleBinaryExp(node, new AMapOverrideBinaryExpCG(), question);
		}

		question.addUnsupportedNode(node, "Expected sequence or map type for '++' binary expression but got: "
				+ leftType);
		return null;
	}

	@Override
	public SExpCG caseAMapEnumMapExp(AMapEnumMapExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		AEnumMapExpCG enumMap = new AEnumMapExpCG();
		enumMap.setType(typeCg);

		LinkedList<AMapletExp> members = node.getMembers();
		for (PExp member : members)
		{
			SExpCG exp = member.apply(question.getExpVisitor(), question);

			if (!(exp instanceof AMapletExpCG))
			{
				question.addUnsupportedNode(node,
						"Got expected map enumeration member: " + exp);
				return null;
			}

			enumMap.getMembers().add((AMapletExpCG) exp);
		}

		return enumMap;
	}

	@Override
	public SExpCG caseAMapletExp(AMapletExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		PExp left = node.getLeft();
		PExp right = node.getRight();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		SExpCG leftCg = left.apply(question.getExpVisitor(), question);
		SExpCG rightCg = right.apply(question.getExpVisitor(), question);

		AMapletExpCG maplet = new AMapletExpCG();
		maplet.setType(typeCg);
		maplet.setLeft(leftCg);
		maplet.setRight(rightCg);

		return maplet;
	}

	@Override
	public SExpCG caseAMapCompMapExp(AMapCompMapExp node, IRInfo question)
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
			if (!(multipleBind instanceof ASetMultipleBind))
			{
				question.addUnsupportedNode(node, "Generation of a map comprehension is only supported for multiple set binds. Got: "
						+ multipleBind);
				return null;
			}

			SMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);

			if (!(multipleBindCg instanceof ASetMultipleBindCG))
			{
				question.addUnsupportedNode(node, "Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: "
						+ multipleBindCg);
			}

			bindingsCg.add((ASetMultipleBindCG) multipleBindCg);
		}

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG firstCg = first.apply(question.getExpVisitor(), question);
		SExpCG predicateCg = predicate != null ? predicate.apply(question.getExpVisitor(), question)
				: null;

		if (!(firstCg instanceof AMapletExpCG))
		{
			question.addUnsupportedNode(node, "Generation of map comprehension expected a maplet expression. Got: "
					+ firstCg);
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
	public SExpCG caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapDomainUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAMapRangeUnaryExp(AMapRangeUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapRangeUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AMapUnionBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistMergeUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ADomainResToBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ADomainResByBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ARangeResToBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ARangeResByBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAMapInverseUnaryExp(AMapInverseUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapInverseUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAEqualsBinaryExp(AEqualsBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AEqualsBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ANotEqualsBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAIndicesUnaryExp(AIndicesUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AIndicesUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseASeqEnumSeqExp(ASeqEnumSeqExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		AEnumSeqExpCG enumSeq = new AEnumSeqExpCG();

		if (type instanceof SSeqType)
		{
			STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
			enumSeq.setType(typeCg);
		} else
		{
			question.addUnsupportedNode(node, "Unexpected sequence type for sequence enumeration expression: "
					+ type.getClass().getName());
			return null;
		}

		LinkedList<PExp> members = node.getMembers();
		for (PExp member : members)
		{
			SExpCG memberCg = member.apply(question.getExpVisitor(), question);
			enumSeq.getMembers().add(memberCg);
		}

		return enumSeq;
	}

	@Override
	public SExpCG caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, IRInfo question)
			throws AnalysisException
	{
		return null;// Indicates an abstract body
	}

	@Override
	public SExpCG caseAFieldExp(AFieldExp node, IRInfo question)
			throws AnalysisException
	{
		SExpCG object = node.getObject().apply(question.getExpVisitor(), question);
		STypeCG type = node.getType().apply(question.getTypeVisitor(), question);

		String memberName = "";

		if (node.getMemberName() != null)
		{
			memberName = node.getMemberName().getFullName();
		} else
		{
			memberName = node.getField().getName();
		}

		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setObject(object);
		fieldExp.setMemberName(memberName);
		fieldExp.setType(type);

		return fieldExp;
	}

	@Override
	public SExpCG caseAApplyExp(AApplyExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp root = node.getRoot();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG rootCg = root.apply(question.getExpVisitor(), question);

		AApplyExpCG applyExp = new AApplyExpCG();
		applyExp.setType(typeCg);
		applyExp.setRoot(rootCg);

		for (PExp arg : node.getArgs())
		{
			SExpCG argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg == null)
			{
				question.addUnsupportedNode(node, "Apply expression is not supported for the argument: "
						+ arg);
				return null;
			}

			applyExp.getArgs().add(argCg);
		}

		return applyExp;
	}

	@Override
	public SExpCG caseAVariableExp(AVariableExp node, IRInfo question)
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

		boolean isDefInOwningClass = inOwningClass
				&& (isLocalDef || isInstanceVarDef || isExplOp || isExplFunc || isAssignmentDef);

		boolean explicit = node.getName().getExplicit();
		boolean isImplicit = !explicit;

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		PTypeAssistantTC typeAssistant = question.getTcFactory().createPTypeAssistant();

		boolean isLambda = typeAssistant.isFunction(type)
				&& !(varDef instanceof SFunctionDefinition);

		boolean isInheritedDef = varDef instanceof AInheritedDefinition;

		if (owningClass == null
				|| nodeParentClass == null
				|| isDefInOwningClass
				|| isInheritedDef
				|| isImplicit
				|| explicit
				&& !question.getTcFactory().createPDefinitionAssistant().isStatic(varDef))
		{
			AIdentifierVarExpCG varExp = new AIdentifierVarExpCG();

			varExp.setType(typeCg);
			varExp.setOriginal(name);
			varExp.setIsLambda(isLambda);

			return varExp;
		} else if (explicit)
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
		} else
		{
			question.addUnsupportedNode(node, "Reached unexpected case when generating a variable expression");
			return null;
		}
	}

	@Override
	public SExpCG caseANewExp(ANewExp node, IRInfo question)
			throws AnalysisException
	{
		String className = node.getClassdef().getName().getName();
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(null);
		typeName.setName(className);

		PType type = node.getType();
		LinkedList<PExp> nodeArgs = node.getArgs();

		ANewExpCG newExp = new ANewExpCG();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		newExp.setType(typeCg);
		newExp.setName(typeName);

		LinkedList<SExpCG> newExpArgs = newExp.getArgs();
		for (PExp arg : nodeArgs)
		{
			newExpArgs.add(arg.apply(question.getExpVisitor(), question));
		}

		return newExp;
	}

	@Override
	public SExpCG caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ATimesNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new APlusNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASubtractNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AGreaterEqualNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAStarStarBinaryExp(AStarStarBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new APowerNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AGreaterNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ALessEqualNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ALessNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		ADivideNumericBinaryExpCG divide = (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);

		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();

		SExpCG leftExpCG = divide.getLeft();

		if (question.getExpAssistant().isIntegerType(leftExp)
				&& question.getExpAssistant().isIntegerType(rightExp))
		{
			ARealLiteralExpCG one = new ARealLiteralExpCG();
			ARealNumericBasicTypeCG realTypeCg = new ARealNumericBasicTypeCG();
			realTypeCg.setSourceNode(new SourceNode(new ARealNumericBasicType()));
			one.setType(realTypeCg);
			one.setValue(1.0);

			ATimesNumericBinaryExpCG neutralMul = new ATimesNumericBinaryExpCG();
			neutralMul.setType(realTypeCg.clone());
			neutralMul.setLeft(one);
			neutralMul.setRight(leftExpCG);

			divide.setLeft(question.getExpAssistant().isolateExpression(neutralMul));
		}

		return divide;
	}

	@Override
	public SExpCG caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		// VDM Language Reference Manual:
		// x mod y = x - y * floor(x/y)

		PType type = node.getType();
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		ADivideNumericBinaryExpCG div = (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);
		AFloorUnaryExpCG floor = new AFloorUnaryExpCG();
		floor.setType(typeCg);
		floor.setExp(div);

		SExpCG leftExpCg = leftExp.apply(question.getExpVisitor(), question);
		SExpCG rightExpCg = rightExp.apply(question.getExpVisitor(), question);

		ATimesNumericBinaryExpCG times = new ATimesNumericBinaryExpCG();
		times.setType(typeCg.clone());
		times.setLeft(rightExpCg);
		times.setRight(floor);

		ASubtractNumericBinaryExpCG sub = new ASubtractNumericBinaryExpCG();
		sub.setType(typeCg.clone());
		sub.setLeft(leftExpCg);
		sub.setRight(times);

		return node.parent() instanceof SBinaryExp ? question.getExpAssistant().isolateExpression(sub)
				: sub;
	}

	@Override
	public SExpCG caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		// VDM Language Reference Manual:
		// x rem y = x - y * (x div y)

		PType type = node.getType();
		PExp leftExp = node.getLeft();
		PExp rightExp = node.getRight();

		ADivideNumericBinaryExpCG div = (ADivideNumericBinaryExpCG) question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpCG(), question);

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG leftExpCg = leftExp.apply(question.getExpVisitor(), question);
		SExpCG rightExpCg = rightExp.apply(question.getExpVisitor(), question);

		ATimesNumericBinaryExpCG times = new ATimesNumericBinaryExpCG();
		times.setType(typeCg);
		times.setLeft(rightExpCg);
		times.setRight(question.getExpAssistant().isolateExpression(div));

		ASubtractNumericBinaryExpCG sub = new ASubtractNumericBinaryExpCG();
		sub.setType(typeCg.clone());
		sub.setLeft(leftExpCg);
		sub.setRight(times);

		return node.parent() instanceof SBinaryExp ? question.getExpAssistant().isolateExpression(sub)
				: sub;
	}

	@Override
	public SExpCG caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		// A => B is constructed as !A || B

		STypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		SExpCG leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		SExpCG rightExpCg = node.getRight().apply(question.getExpVisitor(), question);

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
	public SExpCG caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		// A <=> B is constructed as !(A ^ B)
		STypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		SExpCG leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		SExpCG rightExpCg = node.getRight().apply(question.getExpVisitor(), question);

		AXorBoolBinaryExpCG xorExp = new AXorBoolBinaryExpCG();
		xorExp.setType(typeCg);
		xorExp.setLeft(leftExpCg);
		xorExp.setRight(rightExpCg);

		ANotUnaryExpCG notExp = new ANotUnaryExpCG();
		notExp.setType(typeCg.clone());
		notExp.setExp(question.getExpAssistant().isolateExpression(xorExp));

		return notExp;
	}

	// Unary

	@Override
	public SExpCG caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new APlusUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMinusUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAFloorUnaryExp(AFloorUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AFloorUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AAbsUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseANotUnaryExp(ANotUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ANotUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AOrBoolBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AAndBoolBinaryExpCG(), question);
	}

	@Override
	public SExpCG caseALenUnaryExp(ALenUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ASizeUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAElementsUnaryExp(AElementsUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AElemsUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseAHeadUnaryExp(AHeadUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AHeadUnaryExpCG(), question);
	}

	@Override
	public SExpCG caseATailUnaryExp(ATailUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ATailUnaryExpCG(), question);
	}

	// Literals
	// NOTE: The methods for handling of literals/constants look very similar and ideally should be
	// generalized in a method. However the nodes in the VDM AST don't share a parent with method
	// setValue at the current time of writing.

	@Override
	public SExpCG caseABooleanConstExp(ABooleanConstExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		boolean value = node.getValue().getValue();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		ABoolLiteralExpCG boolLitCg = new ABoolLiteralExpCG();
		boolLitCg.setType(typeCg);
		boolLitCg.setValue(value);

		return boolLitCg;
	}

	@Override
	public SExpCG caseARealLiteralExp(ARealLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		double value = node.getValue().getValue();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		ARealLiteralExpCG realLitCg = new ARealLiteralExpCG();
		realLitCg.setType(typeCg);
		realLitCg.setValue(value);

		return realLitCg;
	}

	@Override
	public SExpCG caseAIntLiteralExp(AIntLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		long value = node.getValue().getValue();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		AIntLiteralExpCG intLitCg = new AIntLiteralExpCG();
		intLitCg.setType(typeCg);
		intLitCg.setValue(value);

		return intLitCg;
	}

	@Override
	public SExpCG caseACharLiteralExp(ACharLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		char value = node.getValue().getValue();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		ACharLiteralExpCG charLitCg = new ACharLiteralExpCG();
		charLitCg.setType(typeCg);
		charLitCg.setValue(value);

		return charLitCg;
	}

	@Override
	public SExpCG caseAStringLiteralExp(AStringLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();
		if (question.getSettings().getCharSeqAsString())
		{
			PType type = node.getType();

			STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

			AStringLiteralExpCG stringLiteral = new AStringLiteralExpCG();

			stringLiteral.setType(typeCg);
			stringLiteral.setIsNull(false);
			stringLiteral.setValue(value);

			return stringLiteral;

		} else
		{
			STypeCG seqTypeCg = node.getType().apply(question.getTypeVisitor(), question);
			return question.getExpAssistant().consCharSequence(seqTypeCg, value);
		}
	}

	@Override
	public SExpCG caseAQuoteLiteralExp(AQuoteLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();
		STypeCG type = node.getType().apply(question.getTypeVisitor(), question);

		AQuoteLiteralExpCG quoteLit = new AQuoteLiteralExpCG();
		quoteLit.setValue(value);
		quoteLit.setType(type);

		question.registerQuoteValue(value);

		return quoteLit;
	}

	@Override
	public SExpCG caseALambdaExp(ALambdaExp node, IRInfo question)
			throws AnalysisException
	{
		LinkedList<ATypeBind> bindList = node.getBindList();
		PExp exp = node.getExpression();
		PType type = node.getType();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);

		ALambdaExpCG lambdaExp = new ALambdaExpCG();

		lambdaExp.setType(typeCg);
		lambdaExp.setExp(expCg);

		LinkedList<AFormalParamLocalParamCG> params = lambdaExp.getParams();

		for (ATypeBind typeBind : bindList)
		{
			PType bindType = typeBind.getType();
			PPattern pattern = typeBind.getPattern();

			if (!(pattern instanceof AIdentifierPattern))
			{
				question.addUnsupportedNode(node, "Expected identifier pattern for lambda expression. Got: "
						+ pattern);
				return null;
			}

			STypeCG bindTypeCg = bindType.apply(question.getTypeVisitor(), question);
			SPatternCG patternCg = pattern.apply(question.getPatternVisitor(), question);

			AFormalParamLocalParamCG param = new AFormalParamLocalParamCG();
			param.setPattern(patternCg);
			param.setType(bindTypeCg);

			params.add(param);
		}

		return lambdaExp;
	}
}
