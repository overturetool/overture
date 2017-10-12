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
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.intf.lex.ILexToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.SSetType;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SBindIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SModifierIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.ASeqBindIR;
import org.overture.codegen.ir.patterns.ASetBindIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.utils.AHeaderLetBeStIR;
import org.overture.config.Settings;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class ExpVisitorIR extends AbstractVisitorIR<IRInfo, SExpIR>
{
	@Override
	public SExpIR caseANarrowExp(ANarrowExp node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getTest();
		PType type = null;

		if (node.getBasicType() != null)
		{
			type = node.getBasicType();
		} else if (node.getTypedef() != null)
		{
			type = question.getTcFactory().createPDefinitionAssistant().getType(node.getTypedef());
		}

		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		STypeIR typeCg;
		if (type != null)
		{
			typeCg = type.apply(question.getTypeVisitor(), question);
		} else
		{
			log.error("Could not find type of narrow expression");
			typeCg = new AUnknownTypeIR();
			typeCg.setSourceNode(new SourceNode(node));
		}

		ACastUnaryExpIR cast = new ACastUnaryExpIR();
		cast.setExp(expCg);
		cast.setType(typeCg);

		return cast;
	}

	@Override
	public SExpIR caseAStateInitExp(AStateInitExp node, IRInfo question)
			throws AnalysisException
	{
		return node.getState().getInitExpression().apply(question.getExpVisitor(), question);
	}

	@Override
	public SExpIR caseAUndefinedExp(AUndefinedExp node, IRInfo question)
			throws AnalysisException
	{
		return new AUndefinedExpIR();
	}

	@Override
	public SExpIR caseAPreOpExp(APreOpExp node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExpression();

		return exp.apply(question.getExpVisitor(), question);
	}

	@Override
	public SExpIR caseAPostOpExp(APostOpExp node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getPostexpression();

		return exp.apply(question.getExpVisitor(), question);
	}

	@Override
	public SExpIR caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			IRInfo question) throws AnalysisException
	{
		return new ANotImplementedExpIR();
	}

	@Override
	public SExpIR caseATimeExp(ATimeExp node, IRInfo question)
			throws AnalysisException
	{
		STypeIR typeCg = node.getType().apply(question.getTypeVisitor(), question);

		ATimeExpIR timeExp = new ATimeExpIR();
		timeExp.setType(typeCg);

		return timeExp;
	}

	@Override
	public SExpIR caseAThreadIdExp(AThreadIdExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		STypeIR typeIR = type.apply(question.getTypeVisitor(), question);

		AThreadIdExpIR threadId = new AThreadIdExpIR();
		threadId.setType(typeIR);

		return threadId;

	}

	@Override
	public SExpIR caseANilExp(ANilExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		ANullExpIR nullExpCg = new ANullExpIR();
		nullExpCg.setType(typeCg);

		return nullExpCg;
	}

	@Override
	public SExpIR caseAMkBasicExp(AMkBasicExp node, IRInfo question)
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

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR argCg = arg.apply(question.getExpVisitor(), question);

		AMkBasicExpIR mkBasicExp = new AMkBasicExpIR();
		mkBasicExp.setType(typeCg);
		mkBasicExp.setArg(argCg);

		return mkBasicExp;
	}

	@Override
	public SExpIR caseAIsExp(AIsExp node, IRInfo question)
			throws AnalysisException
	{
		PType checkedType = node.getBasicType();

		if (checkedType == null)
		{
			checkedType = question.getTcFactory().createPDefinitionAssistant().getType(node.getTypedef());
		}

		PExp exp = node.getTest();
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		if (expCg == null)
		{
			return null;
		}

		STypeIR checkedTypeCg = checkedType.apply(question.getTypeVisitor(), question);

		if (checkedTypeCg == null)
		{
			return null;
		}

		AGeneralIsExpIR generalIsExp = new AGeneralIsExpIR();
		generalIsExp.setType(new ABoolBasicTypeIR());
		generalIsExp.setExp(expCg);
		generalIsExp.setCheckedType(checkedTypeCg);

		return generalIsExp;
	}

	@Override
	public SExpIR caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ACardUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAInSetBinaryExp(AInSetBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AInSetBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().negate(question.getExpAssistant().handleBinaryExp(node, new AInSetBinaryExpIR(), question));
	}

	@Override
	public SExpIR caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetUnionBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetIntersectBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetDifferenceBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseASubsetBinaryExp(ASubsetBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetSubsetBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASetProperSubsetBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistUnionUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistIntersectUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAPowerSetUnaryExp(APowerSetUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new APowerSetUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseASetEnumSetExp(ASetEnumSetExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		if (!(type instanceof SSetType))
		{
			log.error("Unexpected set type for set enumeration expression: "
					+ type.getClass().getName() + " at " + node.getLocation());
		}

		LinkedList<PExp> members = node.getMembers();

		AEnumSetExpIR enumSet = new AEnumSetExpIR();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		enumSet.setType(typeCg);
		LinkedList<SExpIR> membersCg = enumSet.getMembers();

		for (PExp member : members)
		{
			SExpIR memberCg = member.apply(question.getExpVisitor(), question);
			if (memberCg != null)
			{
				membersCg.add(memberCg);
			} else
			{
				return null;
			}
		}

		return enumSet;
	}

	@Override
	public SExpIR caseAForAllExp(AForAllExp node, IRInfo question)
			throws AnalysisException
	{
		// The inheritance hierarchy of the VDM AST tree is structured such that the bindings and the predicate
		// must also be passed to the method that handles the forall and the exists quantifiers
		return question.getExpAssistant().handleQuantifier(node, node.getBindList(), node.getPredicate(), new AForAllQuantifierExpIR(), question, "forall expression");
	}

	@Override
	public SExpIR caseAExistsExp(AExistsExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleQuantifier(node, node.getBindList(), node.getPredicate(), new AExistsQuantifierExpIR(), question, "exists expression");
	}

	@Override
	public SExpIR caseAIotaExp(AIotaExp node, IRInfo question) throws AnalysisException {

		PBind bind = node.getBind();
		SBindIR bindCg = bind.apply(question.getBindVisitor(), question);

		PType type = node.getType();
		PExp predicate = node.getPredicate();

		SMultipleBindIR multipleBind = question.getBindAssistant().convertToMultipleBind(bindCg);

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR predicateCg = predicate.apply(question.getExpVisitor(), question);

		AIotaExpIR exists1Exp = new AIotaExpIR();
		exists1Exp.getBindList().add(multipleBind);
		exists1Exp.setType(typeCg);
		exists1Exp.setPredicate(predicateCg);

		return exists1Exp;
	}

	@Override
	public SExpIR caseAExists1Exp(AExists1Exp node, IRInfo question)
			throws AnalysisException
	{
		PBind bind = node.getBind();
		SBindIR bindCg = bind.apply(question.getBindVisitor(), question);

		PType type = node.getType();
		PExp predicate = node.getPredicate();

		SMultipleBindIR multipleBind = question.getBindAssistant().convertToMultipleBind(bindCg);

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR predicateCg = predicate.apply(question.getExpVisitor(), question);

		AExists1QuantifierExpIR exists1Exp = new AExists1QuantifierExpIR();
		exists1Exp.getBindList().add(multipleBind);
		exists1Exp.setType(typeCg);
		exists1Exp.setPredicate(predicateCg);

		return exists1Exp;
	}

	@Override
	public SExpIR caseASetCompSetExp(ASetCompSetExp node, IRInfo question)
			throws AnalysisException
	{
		LinkedList<PMultipleBind> bindings = node.getBindings();

		List<SMultipleBindIR> bindingsCg = new LinkedList<SMultipleBindIR>();
		for (PMultipleBind multipleBind : bindings)
		{
			SMultipleBindIR multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);

			if (multipleBindCg != null)
			{
				bindingsCg.add(multipleBindCg);
			} else
			{
				return null;
			}
		}

		PType type = node.getType();
		PExp first = node.getFirst();
		PExp predicate = node.getPredicate();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR firstCg = first.apply(question.getExpVisitor(), question);
		SExpIR predicateCg = predicate != null
				? predicate.apply(question.getExpVisitor(), question) : null;

		ACompSetExpIR setComp = new ACompSetExpIR();
		setComp.setBindings(bindingsCg);
		setComp.setType(typeCg);
		setComp.setFirst(firstCg);
		setComp.setPredicate(predicateCg);

		return setComp;
	}

	@Override
	public SExpIR caseASetRangeSetExp(ASetRangeSetExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp firstExp = node.getFirst();
		PExp lastExp = node.getLast();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR firstExpCg = firstExp.apply(question.getExpVisitor(), question);
		SExpIR lastExpCg = lastExp.apply(question.getExpVisitor(), question);

		ARangeSetExpIR setRange = new ARangeSetExpIR();

		setRange.setType(typeCg);
		setRange.setFirst(firstExpCg);
		setRange.setLast(lastExpCg);

		return setRange;
	}

	@Override
	public SExpIR caseACasesExp(ACasesExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp exp = node.getExpression();
		PExp others = node.getOthers();
		LinkedList<ACaseAlternative> cases = node.getCases();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);
		SExpIR othersCg = others != null
				? others.apply(question.getExpVisitor(), question) : null;

		ACasesExpIR casesExpCg = new ACasesExpIR();
		casesExpCg.setType(typeCg);
		casesExpCg.setExp(expCg);
		casesExpCg.setOthers(othersCg);

		question.getExpAssistant().handleAlternativesCasesExp(question, exp, cases, casesExpCg.getCases());

		return casesExpCg;
	}

	@Override
	public SExpIR caseACaseAlternative(ACaseAlternative node, IRInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		PExp result = node.getResult();

		SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);
		SExpIR resultCg = result.apply(question.getExpVisitor(), question);

		ACaseAltExpExpIR altCg = new ACaseAltExpExpIR();

		altCg.setPattern(patternCg);
		altCg.setResult(resultCg);

		return altCg;
	}

	@Override
	public SExpIR caseAIfExp(AIfExp node, IRInfo question)
			throws AnalysisException
	{
		SExpIR testExp = node.getTest().apply(question.getExpVisitor(), question);
		SExpIR thenExp = node.getThen().apply(question.getExpVisitor(), question);
		STypeIR expectedType = node.getType().apply(question.getTypeVisitor(), question);

		ATernaryIfExpIR ternaryIf = new ATernaryIfExpIR();

		ternaryIf.setCondition(testExp);
		ternaryIf.setTrueValue(thenExp);
		ternaryIf.setType(expectedType);

		LinkedList<AElseIfExp> elseExpList = node.getElseList();

		ATernaryIfExpIR nextTernaryIf = ternaryIf;

		for (AElseIfExp currentElseExp : elseExpList)
		{
			ATernaryIfExpIR tmp = new ATernaryIfExpIR();

			testExp = currentElseExp.getElseIf().apply(question.getExpVisitor(), question);
			thenExp = currentElseExp.getThen().apply(question.getExpVisitor(), question);
			expectedType = currentElseExp.getType().apply(question.getTypeVisitor(), question);

			tmp.setCondition(testExp);
			tmp.setTrueValue(thenExp);
			tmp.setType(expectedType);

			nextTernaryIf.setFalseValue(tmp);
			nextTernaryIf = tmp;

		}

		SExpIR elseExp = node.getElse().apply(question.getExpVisitor(), question);
		nextTernaryIf.setFalseValue(elseExp);

		if (node.parent() instanceof SBinaryExp)
		{
			return question.getExpAssistant().isolateExpression(ternaryIf);
		}

		return ternaryIf;
	}

	@Override
	public SExpIR caseATupleExp(ATupleExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		LinkedList<PExp> args = node.getArgs();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		ATupleExpIR tupleExp = new ATupleExpIR();
		tupleExp.setType(typeCg);

		for (PExp exp : args)
		{
			SExpIR expCg = exp.apply(question.getExpVisitor(), question);

			if (expCg != null)
			{
				tupleExp.getArgs().add(expCg);
			} else
			{
				return null;
			}
		}

		return tupleExp;
	}

	@Override
	public SExpIR caseAFieldNumberExp(AFieldNumberExp node, IRInfo question)
			throws AnalysisException
	{
		long fieldCg = node.getField().getValue();
		PType type = node.getType();
		PExp tuple = node.getTuple();

		AFieldNumberExpIR fieldNoExp = new AFieldNumberExpIR();
		SExpIR tupleCg = tuple.apply(question.getExpVisitor(), question);
		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		fieldNoExp.setField(fieldCg);
		fieldNoExp.setType(typeCg);
		fieldNoExp.setTuple(tupleCg);

		return fieldNoExp;
	}

	@Override
	public SExpIR caseAFuncInstatiationExp(AFuncInstatiationExp node,
			IRInfo question) throws AnalysisException
	{
		PType type = node.getType();
		PExp func = node.getFunction();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR funcCg = func.apply(question.getExpVisitor(), question);

		AMethodInstantiationExpIR methodInst = new AMethodInstantiationExpIR();

		LinkedList<PType> actualTypes = node.getActualTypes();
		for (PType actualType : actualTypes)
		{
			STypeIR actualTypeCg = actualType.apply(question.getTypeVisitor(), question);

			if (actualTypeCg != null)
			{
				methodInst.getActualTypes().add(actualTypeCg);
			} else
			{
				return null;
			}
		}

		methodInst.setFunc(funcCg);
		methodInst.setType(typeCg);

		return methodInst;
	}

	@Override
	public SExpIR caseALetBeStExp(ALetBeStExp node, IRInfo question)
			throws AnalysisException
	{
		PMultipleBind multipleBind = node.getBind();
		SMultipleBindIR multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);

		PType type = node.getType();
		PExp suchThat = node.getSuchThat();
		PExp value = node.getValue();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR suchThatCg = suchThat != null
				? suchThat.apply(question.getExpVisitor(), question) : null;
		SExpIR valueCg = value.apply(question.getExpVisitor(), question);

		ALetBeStExpIR letBeStExp = new ALetBeStExpIR();

		AHeaderLetBeStIR header = question.getExpAssistant().consHeader(multipleBindCg, suchThatCg);

		letBeStExp.setType(typeCg);
		letBeStExp.setHeader(header);
		letBeStExp.setValue(valueCg);

		return letBeStExp;
	}

	@Override
	public SExpIR caseALetDefExp(ALetDefExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp exp = node.getExpression();

		ALetDefExpIR letDefExp = new ALetDefExpIR();

		question.getDeclAssistant().setFinalLocalDefs(node.getLocalDefs(), letDefExp.getLocalDefs(), question);

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		letDefExp.setType(typeCg);

		SExpIR expCg = exp.apply(question.getExpVisitor(), question);
		letDefExp.setExp(expCg);

		return letDefExp;
	}

	@Override
	public SExpIR caseAMuExp(AMuExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		ARecordInvariantType recType = node.getRecordType();
		PExp rec = node.getRecord();
		List<ARecordModifier> modifiers = node.getModifiers();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		STypeIR recTypeCg = recType.apply(question.getTypeVisitor(), question);

		if (!(recTypeCg instanceof ARecordTypeIR))
		{
			question.addUnsupportedNode(node, "Expected a record type. Got: "
					+ recTypeCg);
			return null;
		}

		SExpIR recCg = rec.apply(question.getExpVisitor(), question);

		List<ARecordModifierIR> modifiersCg = new LinkedList<ARecordModifierIR>();

		for (ARecordModifier m : modifiers)
		{
			SModifierIR modifier = m.apply(question.getModifierVisitor(), question);

			if (modifier instanceof ARecordModifierIR)
			{
				modifiersCg.add((ARecordModifierIR) modifier);
			} else
			{
				question.addUnsupportedNode(node, "Expected modifier to be a record modifier for the 'mu' expression. Got: "
						+ modifier);
				return null;
			}

		}

		ARecordModExpIR recModExp = new ARecordModExpIR();
		recModExp.setType(typeCg);
		recModExp.setRecType((ARecordTypeIR) recTypeCg);
		recModExp.setRec(recCg);
		recModExp.setModifiers(modifiersCg);

		return recModExp;
	}

	@Override
	public SExpIR caseAMkTypeExp(AMkTypeExp node, IRInfo question)
			throws AnalysisException
	{
		ARecordInvariantType recType = node.getRecordType();

		if (recType == null)
		{
			question.addUnsupportedNode(node, "Expected record type for mk_<type> expression. Got null.");
			return null;
		}

		STypeIR typeCg = recType.apply(question.getTypeVisitor(), question);

		if (!(typeCg instanceof ARecordTypeIR))
		{
			question.addUnsupportedNode(node, "Expected record type but got: "
					+ typeCg.getClass().getName() + " in 'mk_' expression");
			return null;
		}

		ARecordTypeIR recordTypeCg = (ARecordTypeIR) typeCg;

		LinkedList<PExp> nodeArgs = node.getArgs();

		ANewExpIR newExp = new ANewExpIR();
		newExp.setType(recordTypeCg);
		newExp.setName(recordTypeCg.getName().clone());

		LinkedList<SExpIR> newExpArgs = newExp.getArgs();

		for (PExp arg : nodeArgs)
		{
			SExpIR argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg != null)
			{
				newExpArgs.add(argCg);
			} else
			{
				return null;
			}
		}

		return newExp;
	}

	@Override
	public SExpIR caseASelfExp(ASelfExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		ASelfExpIR selfExpCg = new ASelfExpIR();
		selfExpCg.setType(typeCg);

		return selfExpCg;
	}

	@Override
	public SExpIR caseASubseqExp(ASubseqExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp from = node.getFrom();
		PExp to = node.getTo();
		PExp seq = node.getSeq();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR fromCg = from.apply(question.getExpVisitor(), question);
		SExpIR toCg = to.apply(question.getExpVisitor(), question);
		SExpIR seqCg = seq.apply(question.getExpVisitor(), question);

		ASubSeqExpIR subSeq = new ASubSeqExpIR();
		subSeq.setType(typeCg);
		subSeq.setFrom(fromCg);
		subSeq.setTo(toCg);
		subSeq.setSeq(seqCg);

		return subSeq;
	}

	@Override
	public SExpIR caseAReverseUnaryExp(AReverseUnaryExp node, IRInfo question)
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

		STypeIR seqTypeCg = seqType.apply(question.getTypeVisitor(), question);
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		AReverseUnaryExpIR reverse = new AReverseUnaryExpIR();
		reverse.setExp(expCg);
		reverse.setType(seqTypeCg);

		return reverse;
	}

	@Override
	public SExpIR caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistConcatUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseASeqCompSeqExp(ASeqCompSeqExp node, IRInfo question)
			throws AnalysisException
	{
		ACompSeqExpIR seqComp = new ACompSeqExpIR();

		if (node.getSetBind() != null)
		{
			ASetBind setBind = node.getSetBind();
			SBindIR bindTempCg = setBind.apply(question.getBindVisitor(), question);

			if (!(bindTempCg instanceof ASetBindIR))
			{
				question.addUnsupportedNode(node, "Expected set bind for sequence comprehension. Got: "
						+ bindTempCg);
				return null;
			}
			ASetBindIR setBindCg = (ASetBindIR) bindTempCg;
			seqComp.setSetBind(setBindCg);

			PExp set = node.getSetBind().getSet();
			SExpIR setCg = set.apply(question.getExpVisitor(), question);
			seqComp.setSetSeq(setCg);
		} else
		{
			ASeqBind seqBind = node.getSeqBind();
			SBindIR bindTempCg = seqBind.apply(question.getBindVisitor(), question);

			if (!(bindTempCg instanceof ASeqBindIR))
			{
				question.addUnsupportedNode(node, "Expected seq bind for sequence comprehension. Got: "
						+ bindTempCg);
				return null;
			}

			ASeqBindIR seqBindCg = (ASeqBindIR) bindTempCg;
			seqComp.setSeqBind(seqBindCg);

			PExp seq = node.getSeqBind().getSeq();
			SExpIR seqCg = seq.apply(question.getExpVisitor(), question);
			seqComp.setSetSeq(seqCg);
		}

		PType type = node.getType();
		PExp first = node.getFirst();

		PExp predicate = node.getPredicate();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR firstCg = first.apply(question.getExpVisitor(), question);

		SExpIR predicateCg = predicate != null
				? predicate.apply(question.getExpVisitor(), question) : null;

		seqComp.setType(typeCg);
		seqComp.setFirst(firstCg);

		seqComp.setPredicate(predicateCg);

		return seqComp;
	}

	@Override
	public SExpIR caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASeqConcatBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		if (node.getType() instanceof SSeqType)
		{
			return question.getExpAssistant().handleBinaryExp(node, new ASeqModificationBinaryExpIR(), question);
		} else if (node.getType() instanceof SMapType)
		{
			return question.getExpAssistant().handleBinaryExp(node, new AMapOverrideBinaryExpIR(), question);
		}

		question.addUnsupportedNode(node, "Expected sequence or map type for '++' binary expression but got: "
				+ node.getType());

		return null;
	}

	@Override
	public SExpIR caseACompBinaryExp(ACompBinaryExp node, IRInfo question) throws AnalysisException {

		PExp left = node.getLeft();
		PExp right = node.getRight();

		SExpIR leftCg = left.apply(question.getExpVisitor(), question);
		SExpIR rightCg = right.apply(question.getExpVisitor(), question);

		ACompBinaryExpIR compExp = new ACompBinaryExpIR();
		compExp.setLeft(leftCg);
		compExp.setRight(rightCg);

		return compExp;
	}

	@Override
	public SExpIR caseAMapEnumMapExp(AMapEnumMapExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		AEnumMapExpIR enumMap = new AEnumMapExpIR();
		enumMap.setType(typeCg);

		LinkedList<AMapletExp> members = node.getMembers();
		for (PExp member : members)
		{
			SExpIR memberCg = member.apply(question.getExpVisitor(), question);

			if (!(memberCg instanceof AMapletExpIR))
			{
				question.addUnsupportedNode(node, "Got expected map enumeration member: "
						+ memberCg);
				return null;
			} else
			{
				enumMap.getMembers().add((AMapletExpIR) memberCg);
			}
		}

		return enumMap;
	}

	@Override
	public SExpIR caseAMapletExp(AMapletExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		PExp left = node.getLeft();
		PExp right = node.getRight();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		SExpIR leftCg = left.apply(question.getExpVisitor(), question);
		SExpIR rightCg = right.apply(question.getExpVisitor(), question);

		AMapletExpIR maplet = new AMapletExpIR();
		maplet.setType(typeCg);
		maplet.setLeft(leftCg);
		maplet.setRight(rightCg);

		return maplet;
	}

	@Override
	public SExpIR caseAMapCompMapExp(AMapCompMapExp node, IRInfo question)
			throws AnalysisException
	{
		LinkedList<PMultipleBind> bindings = node.getBindings();
		PType type = node.getType();
		AMapletExp first = node.getFirst();
		PExp predicate = node.getPredicate();

		List<SMultipleBindIR> bindingsCg = new LinkedList<SMultipleBindIR>();
		for (PMultipleBind multipleBind : bindings)
		{
			SMultipleBindIR multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);

			if (multipleBindCg != null)
			{
				bindingsCg.add(multipleBindCg);
			} else
			{
				return null;
			}
		}

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR firstCg = first.apply(question.getExpVisitor(), question);
		SExpIR predicateCg = predicate != null
				? predicate.apply(question.getExpVisitor(), question) : null;

		if (!(firstCg instanceof AMapletExpIR))
		{
			question.addUnsupportedNode(node, "Generation of map comprehension expected a maplet expression. Got: "
					+ firstCg);
			return null;
		}

		AMapletExpIR mapletExpCg = (AMapletExpIR) firstCg;

		ACompMapExpIR mapComp = new ACompMapExpIR();

		mapComp.setBindings(bindingsCg);
		mapComp.setType(typeCg);
		mapComp.setFirst(mapletExpCg);
		mapComp.setPredicate(predicateCg);

		return mapComp;
	}

	@Override
	public SExpIR caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapDomainUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAMapRangeUnaryExp(AMapRangeUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapRangeUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AMapUnionBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ADistMergeUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ADomainResToBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ADomainResByBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ARangeResToBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ARangeResByBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAMapInverseUnaryExp(AMapInverseUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMapInverseUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAEqualsBinaryExp(AEqualsBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		SExpIR eqBinExpCg = question.getExpAssistant().handleBinaryExp(node, new AEqualsBinaryExpIR(), question);

		// TODO: Update the type checker?
		// PVJ: For some reason the type checker does not decorate the SL
		// init expression (e.g. s = mk_StateName(1,2).) in state
		// definitions with a type (the type is null)
		// So this is really just to prevent null types
		// in the IR
		if (eqBinExpCg.getType() == null)
		{
			eqBinExpCg.setType(new ABoolBasicTypeIR());
		}

		return eqBinExpCg;
	}

	@Override
	public SExpIR caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ANotEqualsBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAIndicesUnaryExp(AIndicesUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AIndicesUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseASeqEnumSeqExp(ASeqEnumSeqExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();

		AEnumSeqExpIR enumSeq = new AEnumSeqExpIR();

		if (type instanceof SSeqType)
		{
			STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
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
			SExpIR memberCg = member.apply(question.getExpVisitor(), question);

			if (memberCg != null)
			{
				enumSeq.getMembers().add(memberCg);
			} else
			{
				return null;
			}
		}

		return enumSeq;
	}

	@Override
	public SExpIR caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, IRInfo question)
			throws AnalysisException
	{
		return null;// Indicates an abstract body
	}

	@Override
	public SExpIR caseAFieldExp(AFieldExp node, IRInfo question)
			throws AnalysisException
	{
		SExpIR object = node.getObject().apply(question.getExpVisitor(), question);
		STypeIR type = node.getType().apply(question.getTypeVisitor(), question);

		String memberName = "";

		if (node.getMemberName() != null)
		{
			memberName = node.getMemberName().getFullName();
		} else
		{
			memberName = node.getField().getName();
		}

		AFieldExpIR fieldExp = new AFieldExpIR();
		fieldExp.setObject(object);
		fieldExp.setMemberName(memberName);
		fieldExp.setType(type);

		return fieldExp;
	}

	@Override
	public SExpIR caseAApplyExp(AApplyExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp root = node.getRoot();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR rootCg = root.apply(question.getExpVisitor(), question);

		AApplyExpIR applyExp = new AApplyExpIR();
		applyExp.setType(typeCg);
		applyExp.setRoot(rootCg);

		for (PExp arg : node.getArgs())
		{
			SExpIR argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg != null)
			{
				applyExp.getArgs().add(argCg);
			} else
			{
				return null;
			}

		}

		return applyExp;
	}

	@Override
	public SExpIR caseAVariableExp(AVariableExp node, IRInfo question)
			throws AnalysisException
	{
		PDefinition varDef = node.getVardef();
		PType type = node.getType();
		String name = node.getName().getName();

		PDefinition unfolded = varDef;

		while (unfolded instanceof AInheritedDefinition)
		{
			unfolded = ((AInheritedDefinition) unfolded).getSuperdef();
		}

		boolean isLambda = question.getTcFactory().createPTypeAssistant().isFunction(type)
				&& !(unfolded instanceof SFunctionDefinition);

		boolean explicit = node.getName().getExplicit();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		boolean isLocalDef = varDef instanceof AAssignmentDefinition
				|| !(varDef.getNameScope() == NameScope.STATE
						|| varDef.getNameScope() == NameScope.GLOBAL
						|| varDef.getNameScope() == NameScope.VARSTATE
						|| varDef.getNameScope() == NameScope.VARSANDSTATE);

		if (Settings.dialect == Dialect.VDM_PP
				|| Settings.dialect == Dialect.VDM_RT)
		{
			SClassDefinition owningClass = varDef.getAncestor(SClassDefinition.class);
			SClassDefinition nodeParentClass = node.getAncestor(SClassDefinition.class);

			boolean isInstanceVarDef = varDef instanceof AInstanceVariableDefinition;
			boolean isExplOp = varDef instanceof SOperationDefinition;
			boolean isExplFunc = varDef instanceof SFunctionDefinition;

			boolean isDefInOwningClass = owningClass == nodeParentClass
					&& (isLocalDef || isInstanceVarDef || isExplOp
							|| isExplFunc);

			if (isExplOp && !isDefInOwningClass
					&& !question.getTcFactory().createPDefinitionAssistant().isStatic(varDef))
			{
				ASuperVarExpIR superVarExp = new ASuperVarExpIR();

				superVarExp.setType(typeCg);
				superVarExp.setIsLocal(isLocalDef);
				superVarExp.setName(name);
				superVarExp.setIsLambda(isLambda);

				return superVarExp;
			} else if (explicit && !isLocalDef
					&& question.getTcFactory().createPDefinitionAssistant().isStatic(varDef))
			{
				return consExplicitVar(node.getName().getModule(), name, isLambda, typeCg, isLocalDef);
			} else
			{
				return consIdVar(name, isLambda, typeCg, isLocalDef);
			}
		} else if (Settings.dialect == Dialect.VDM_SL)
		{
			String defModuleName = varDef.getLocation().getModule();

			String nodeModuleName = "DEFAULT";

			AModuleModules nodeModule = node.getAncestor(AModuleModules.class);

			if (nodeModule != null)
			{
				nodeModuleName = nodeModule.getName().getName();
			}

			boolean inOwningModule = defModuleName.equals(nodeModuleName);

			SVarExpIR res;

			if (inOwningModule)
			{
				res = consIdVar(name, isLambda, typeCg, isLocalDef);
			} else
			{
				res = consExplicitVar(defModuleName, name, isLambda, typeCg, isLocalDef);
			}

			if (question.getDeclAssistant().inFunc(node)
					|| varDef.getAncestor(AStateDefinition.class) == null)
			{
				question.registerSlStateRead(res);
			}

			return res;
		} else
		{
			log.error("Got unexpected dialect " + Settings.dialect);
			return null;
		}
	}

	private SVarExpIR consExplicitVar(String className, String name,
			boolean isLambda, STypeIR typeCg, boolean isLocalDef)
	{
		AExplicitVarExpIR varExp = new AExplicitVarExpIR();

		AClassTypeIR classType = new AClassTypeIR();
		classType.setName(className);

		varExp.setType(typeCg);
		varExp.setIsLocal(isLocalDef);
		varExp.setClassType(classType);
		varExp.setName(name);
		varExp.setIsLambda(isLambda);

		return varExp;
	}

	private SVarExpIR consIdVar(String name, boolean isLambda, STypeIR typeCg,
			boolean isLocalDef)
	{
		AIdentifierVarExpIR varExp = new AIdentifierVarExpIR();

		varExp.setType(typeCg);
		varExp.setIsLocal(isLocalDef);
		varExp.setName(name);
		varExp.setIsLambda(isLambda);

		return varExp;
	}

	@Override
	public SExpIR caseANewExp(ANewExp node, IRInfo question)
			throws AnalysisException
	{
		String className = node.getClassdef().getName().getName();
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(null);
		typeName.setName(className);

		PType type = node.getType();
		LinkedList<PExp> nodeArgs = node.getArgs();

		ANewExpIR newExp = new ANewExpIR();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		newExp.setType(typeCg);
		newExp.setName(typeName);

		LinkedList<SExpIR> newExpArgs = newExp.getArgs();
		for (PExp arg : nodeArgs)
		{
			SExpIR argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg != null)
			{
				newExpArgs.add(argCg);
			} else
			{
				return null;
			}
		}

		return newExp;
	}

	@Override
	public SExpIR caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ATimesNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new APlusNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ASubtractNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AGreaterEqualNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAStarStarBinaryExp(AStarStarBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		PTypeAssistantTC assist = question.getTcFactory().createPTypeAssistant();

		PType lType = node.getLeft().getType();

		if(assist.isMap(lType))
		{
			return question.getExpAssistant().handleBinaryExp(node, new AMapIterationBinaryExpIR(), question);
		}
		else if(assist.isFunction(lType))
		{
			return question.getExpAssistant().handleBinaryExp(node, new AFuncIterationBinaryExpIR(), question);
		}
		else
		{
			// So it must be numeric
			return question.getExpAssistant().handleBinaryExp(node, new APowerNumericBinaryExpIR(), question);
		}
	}

	@Override
	public SExpIR caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AGreaterNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ALessEqualNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ALessNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new ADivideNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return (AIntDivNumericBinaryExpIR) question.getExpAssistant().handleBinaryExp(node, new AIntDivNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAModNumericBinaryExp(AModNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return (AModNumericBinaryExpIR) question.getExpAssistant().handleBinaryExp(node, new AModNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return (ARemNumericBinaryExpIR) question.getExpAssistant().handleBinaryExp(node, new ARemNumericBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		// A => B is constructed as !A || B

		STypeIR typeCg = node.getType().apply(question.getTypeVisitor(), question);
		SExpIR leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		SExpIR rightExpCg = node.getRight().apply(question.getExpVisitor(), question);

		ANotUnaryExpIR notExp = new ANotUnaryExpIR();
		notExp.setType(typeCg);
		notExp.setExp(leftExpCg);

		AOrBoolBinaryExpIR orExp = new AOrBoolBinaryExpIR();
		orExp.setType(typeCg);
		orExp.setLeft(notExp);
		orExp.setRight(rightExpCg);

		return orExp;
	}

	@Override
	public SExpIR caseAEquivalentBooleanBinaryExp(
			AEquivalentBooleanBinaryExp node, IRInfo question)
			throws AnalysisException
	{
		// A <=> B is constructed as !(A ^ B)
		STypeIR typeCg = node.getType().apply(question.getTypeVisitor(), question);
		SExpIR leftExpCg = node.getLeft().apply(question.getExpVisitor(), question);
		SExpIR rightExpCg = node.getRight().apply(question.getExpVisitor(), question);

		AXorBoolBinaryExpIR xorExp = new AXorBoolBinaryExpIR();
		xorExp.setType(typeCg);
		xorExp.setLeft(leftExpCg);
		xorExp.setRight(rightExpCg);

		ANotUnaryExpIR notExp = new ANotUnaryExpIR();
		notExp.setType(typeCg.clone());
		notExp.setExp(question.getExpAssistant().isolateExpression(xorExp));

		return notExp;
	}

	// Unary

	@Override
	public SExpIR caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new APlusUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AMinusUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAFloorUnaryExp(AFloorUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AFloorUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AAbsUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseANotUnaryExp(ANotUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ANotUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AOrBoolBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleBinaryExp(node, new AAndBoolBinaryExpIR(), question);
	}

	@Override
	public SExpIR caseALenUnaryExp(ALenUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ALenUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAElementsUnaryExp(AElementsUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AElemsUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseAHeadUnaryExp(AHeadUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new AHeadUnaryExpIR(), question);
	}

	@Override
	public SExpIR caseATailUnaryExp(ATailUnaryExp node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleUnaryExp(node, new ATailUnaryExpIR(), question);
	}

	// Literals
	// NOTE: The methods for handling of literals/constants look very similar and ideally should be
	// generalized in a method. However the nodes in the VDM AST don't share a parent with method
	// setValue at the current time of writing.

	@Override
	public SExpIR caseABooleanConstExp(ABooleanConstExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		boolean value = node.getValue().getValue();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		ABoolLiteralExpIR boolLitCg = new ABoolLiteralExpIR();
		boolLitCg.setType(typeCg);
		boolLitCg.setValue(value);

		return boolLitCg;
	}

	@Override
	public SExpIR caseARealLiteralExp(ARealLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		double value = node.getValue().getValue();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		ARealLiteralExpIR realLitCg = new ARealLiteralExpIR();
		realLitCg.setType(typeCg);
		realLitCg.setValue(value);

		return realLitCg;
	}

	@Override
	public SExpIR caseAIntLiteralExp(AIntLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		long value = node.getValue().getValue();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		AIntLiteralExpIR intLitCg = new AIntLiteralExpIR();
		intLitCg.setType(typeCg);
		intLitCg.setValue(value);

		return intLitCg;
	}

	@Override
	public SExpIR caseACharLiteralExp(ACharLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		char value = node.getValue().getValue();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		ACharLiteralExpIR charLitCg = new ACharLiteralExpIR();
		charLitCg.setType(typeCg);
		charLitCg.setValue(value);

		return charLitCg;
	}

	@Override
	public SExpIR caseAStringLiteralExp(AStringLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();
		if (question.getSettings().getCharSeqAsString())
		{
			PType type = node.getType();

			STypeIR typeCg;

			/**
			 * The operation argument passed to the 'setPriority' of the CPU class (VDM-RT) gets transformed into a
			 * string literal where the type is missing: CPU1.setPriority(B`op, 4). The check below is really to
			 * cirumvent this issue.
			 */
			if (type != null)
			{
				typeCg = type.apply(question.getTypeVisitor(), question);
			} else
			{
				typeCg = new AStringTypeIR();
			}

			AStringLiteralExpIR stringLiteral = new AStringLiteralExpIR();

			stringLiteral.setType(typeCg);
			stringLiteral.setIsNull(false);
			stringLiteral.setValue(value);

			return stringLiteral;

		} else
		{
			PType type = node.getType();
			STypeIR strType;

			// Same issue with the 'setPriority' operation (see above)
			if (type != null)
			{
				strType = type.apply(question.getTypeVisitor(), question);
			} else
			{
				ASeqSeqTypeIR seqTypeCg = new ASeqSeqTypeIR();
				seqTypeCg.setEmpty(false);
				seqTypeCg.setSeqOf(new ACharBasicTypeIR());
				strType = seqTypeCg;
			}

			return question.getExpAssistant().consCharSequence(strType, value);
		}
	}

	@Override
	public SExpIR caseAQuoteLiteralExp(AQuoteLiteralExp node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();
		STypeIR type = node.getType().apply(question.getTypeVisitor(), question);

		AQuoteLiteralExpIR quoteLit = new AQuoteLiteralExpIR();
		quoteLit.setValue(value);
		quoteLit.setType(type);

		question.registerQuoteValue(value);

		return quoteLit;
	}

	@Override
	public SExpIR caseALambdaExp(ALambdaExp node, IRInfo question)
			throws AnalysisException
	{
		LinkedList<ATypeBind> bindList = node.getBindList();
		PExp exp = node.getExpression();
		PType type = node.getType();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		ALambdaExpIR lambdaExp = new ALambdaExpIR();

		lambdaExp.setType(typeCg);
		lambdaExp.setExp(expCg);

		LinkedList<AFormalParamLocalParamIR> params = lambdaExp.getParams();

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

			STypeIR bindTypeCg = bindType.apply(question.getTypeVisitor(), question);
			SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);

			AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();
			param.setPattern(patternCg);
			param.setType(bindTypeCg);

			params.add(param);
		}

		return lambdaExp;
	}

	@Override
	public SExpIR caseAHistoryExp(AHistoryExp node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		ILexToken hop = node.getHop();

		AClassClassDefinition enclosingClass = node.getAncestor(AClassClassDefinition.class);

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);

		AHistoryExpIR history = new AHistoryExpIR();

		history.setHistype(hop.toString().substring(1));
		history.setType(typeCg);

		AClassTypeIR innerclassType = null;

		if (enclosingClass != null)
		{
			innerclassType = new AClassTypeIR();
			innerclassType.setName(enclosingClass.getName() + "_sentinel");
			history.setSentinelType(innerclassType);
		} else
		{
			String msg = "Enclosing class could not be found for history expression.";
			log.error(msg);
			question.addUnsupportedNode(node, msg);

			return null;
		}

		history.setOpsname(node.getOpnames().getFirst().getName());

		if (node.getOpnames().size() == 1)
		{
			return history;

		} else
		{
			APlusNumericBinaryExpIR historyCounterSum = new APlusNumericBinaryExpIR();
			historyCounterSum.setType(typeCg.clone());
			historyCounterSum.setLeft(history);

			APlusNumericBinaryExpIR last = historyCounterSum;

			for (int i = 1; i < node.getOpnames().size() - 1; i++)
			{

				String nextOpName = node.getOpnames().get(i).toString();

				AHistoryExpIR left = new AHistoryExpIR();
				left.setType(typeCg.clone());
				left.setSentinelType(innerclassType.clone());
				left.setHistype(hop.toString().substring(1));
				left.setOpsname(nextOpName);

				APlusNumericBinaryExpIR tmp = new APlusNumericBinaryExpIR();
				tmp.setType(typeCg.clone());
				tmp.setLeft(left);

				last.setRight(tmp);

				last = tmp;

			}

			String lastOpName = node.getOpnames().getLast().toString();

			AHistoryExpIR lastHistoryExp = new AHistoryExpIR();
			lastHistoryExp.setType(typeCg.clone());
			lastHistoryExp.setSentinelType(innerclassType.clone());
			lastHistoryExp.setHistype(hop.toString().substring(1));
			lastHistoryExp.setOpsname(lastOpName);

			last.setRight(lastHistoryExp);

			return historyCounterSum;
		}
	}

	@Override
	public SExpIR caseAIsOfBaseClassExp(AIsOfBaseClassExp node, IRInfo question) throws AnalysisException {

		String baseClass = node.getBaseClass().getName();
		PExp exp = node.getExp();

		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		AIsOfBaseClassExpIR isOfBaseClassExp = new AIsOfBaseClassExpIR();
		isOfBaseClassExp.setBaseClass(baseClass);
		isOfBaseClassExp.setExp(expCg);

		return isOfBaseClassExp;
	}

	@Override
	public SExpIR caseAIsOfClassExp(AIsOfClassExp node, IRInfo question)
			throws AnalysisException {
		PType type = node.getType();
		AClassType classType = node.getClassType();
		PExp objRef = node.getExp();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		STypeIR classTypeCg = classType.apply(question.getTypeVisitor(), question);

		if (!(classTypeCg instanceof AClassTypeIR)) {
			log.error("Unexpected class type encountered for "
					+ AIsOfClassExp.class.getName() + ". Expected class type: "
					+ AClassTypeIR.class.getName() + ". Got: "
					+ typeCg.getClass().getName() + " at "
					+ node.getLocation());
		}

		SExpIR objRefCg = objRef.apply(question.getExpVisitor(), question);

		AIsOfClassExpIR instanceOfExp = new AIsOfClassExpIR();
		instanceOfExp.setType(typeCg);
		instanceOfExp.setCheckedType(classTypeCg);
		instanceOfExp.setExp(objRefCg);

		return instanceOfExp;
	}

	@Override
	public SExpIR caseASameBaseClassExp(ASameBaseClassExp node, IRInfo question) throws AnalysisException {

		PExp left = node.getLeft();
		PExp right = node.getRight();

		SExpIR leftCg = left.apply(question.getExpVisitor(), question);
		SExpIR rightCg = right.apply(question.getExpVisitor(), question);

		ASameBaseClassExpIR sameBaseClassExp = new ASameBaseClassExpIR();
		sameBaseClassExp.setLeft(leftCg);
		sameBaseClassExp.setRight(rightCg);

		return sameBaseClassExp;
	}

	@Override
	public SExpIR caseASameClassExp(ASameClassExp node, IRInfo question) throws AnalysisException {

		PExp left = node.getLeft();
		PExp right = node.getRight();

		SExpIR leftCg = left.apply(question.getExpVisitor(), question);
		SExpIR rightCg = right.apply(question.getExpVisitor(), question);

		ASameClassExpIR sameBaseClassExp = new ASameClassExpIR();
		sameBaseClassExp.setLeft(leftCg);
		sameBaseClassExp.setRight(rightCg);

		return sameBaseClassExp;
	}
}
