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
package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.ABoolIsExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACaseAltExpExpCG;
import org.overture.codegen.cgast.expressions.ACharIsExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AGeneralIsExpCG;
import org.overture.codegen.cgast.expressions.AIntIsExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.ANat1IsExpCG;
import org.overture.codegen.cgast.expressions.ANatIsExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AQuoteLiteralExpCG;
import org.overture.codegen.cgast.expressions.ARatIsExpCG;
import org.overture.codegen.cgast.expressions.ARealIsExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATokenIsExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SIsExpCG;
import org.overture.codegen.cgast.expressions.SQuantifierExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AWhileStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AQuoteTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.IRInfo;

public class ExpAssistantCG extends AssistantBase
{
	public ExpAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public SExpCG consLetDefExp(PExp node, List<PDefinition> defs, PExp exp,
			PType type, IRInfo question, String message)
			throws AnalysisException
	{
		if (question.getExpAssistant().isAssigned(node))
		{
			question.addUnsupportedNode(node, message);
			return null;
		}

		ALetDefExpCG letDefExp = new ALetDefExpCG();

		question.getDeclAssistant().setLocalDefs(defs, letDefExp.getLocalDefs(), question);

		SExpCG expCg = exp.apply(question.getExpVisitor(), question);
		letDefExp.setExp(expCg);

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		letDefExp.setType(typeCg);

		return letDefExp;
	}

	public SExpCG isolateExpression(SExpCG exp)
	{
		AIsolationUnaryExpCG isolationExp = new AIsolationUnaryExpCG();
		isolationExp.setExp(exp);
		isolationExp.setType(exp.getType().clone());
		return isolationExp;
	}

	public ANotUnaryExpCG negate(SExpCG exp)
	{
		ANotUnaryExpCG negated = new ANotUnaryExpCG();
		negated.setType(exp.getType().clone());
		negated.setExp(exp);

		return negated;
	}

	public SExpCG handleUnaryExp(SUnaryExp vdmExp, SUnaryExpCG codeGenExp,
			IRInfo question) throws AnalysisException
	{
		SExpCG expCg = vdmExp.getExp().apply(question.getExpVisitor(), question);
		STypeCG typeCg = vdmExp.getType().apply(question.getTypeVisitor(), question);

		codeGenExp.setType(typeCg);
		codeGenExp.setExp(expCg);

		return codeGenExp;
	}

	public SExpCG handleBinaryExp(SBinaryExp vdmExp, SBinaryExpCG codeGenExp,
			IRInfo question) throws AnalysisException
	{
		PType type = vdmExp.getType();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		codeGenExp.setType(typeCg);

		PExp vdmExpLeft = vdmExp.getLeft();
		PExp vdmExpRight = vdmExp.getRight();

		SExpCG leftExpCg = vdmExpLeft.apply(question.getExpVisitor(), question);
		SExpCG rightExpCg = vdmExpRight.apply(question.getExpVisitor(), question);

		codeGenExp.setLeft(leftExpCg);
		codeGenExp.setRight(rightExpCg);

		return codeGenExp;
	}

	public boolean isIntegerType(PExp exp)
	{
		PType type = exp.getType();

		// Expressions like 1.0 are considered real literal expressions
		// of type NatOneNumericBasicType

		return (type instanceof ANatOneNumericBasicType
				|| type instanceof ANatNumericBasicType || type instanceof AIntNumericBasicType)
				&& !(exp instanceof ARealLiteralExp);
	}

	public ABoolLiteralExpCG consBoolLiteral(boolean val)
	{
		ABoolLiteralExpCG boolLiteral = new ABoolLiteralExpCG();
		boolLiteral.setType(new ABoolBasicTypeCG());
		boolLiteral.setValue(val);

		return boolLiteral;
	}

	public AIntLiteralExpCG consIntLiteral(long value)
	{
		AIntLiteralExpCG intLiteral = new AIntLiteralExpCG();
		intLiteral.setType(new AIntNumericBasicTypeCG());
		intLiteral.setValue(value);

		return intLiteral;
	}

	public ARealLiteralExpCG consRealLiteral(double value)
	{
		ARealLiteralExpCG realLiteral = new ARealLiteralExpCG();
		realLiteral.setType(new ARealNumericBasicTypeCG());
		realLiteral.setValue(value);

		return realLiteral;
	}

	public ACharLiteralExpCG consCharLiteral(char value)
	{
		ACharLiteralExpCG charLiteral = new ACharLiteralExpCG();
		charLiteral.setType(new ACharBasicTypeCG());
		charLiteral.setValue(value);

		return charLiteral;
	}

	public AStringLiteralExpCG consStringLiteral(String value, boolean isNull)
	{
		AStringLiteralExpCG stringLiteral = new AStringLiteralExpCG();

		stringLiteral.setType(new AStringTypeCG());
		stringLiteral.setIsNull(isNull);
		stringLiteral.setValue(value);

		return stringLiteral;
	}

	public SExpCG consCharSequence(STypeCG seqType, String value)
	{
		AEnumSeqExpCG enumSeq = new AEnumSeqExpCG();

		enumSeq.setType(seqType);

		for (int i = 0; i < value.length(); i++)
		{
			char currentChar = value.charAt(i);
			ACharLiteralExpCG charLit = new ACharLiteralExpCG();
			charLit.setType(new ACharBasicTypeCG());
			charLit.setValue(currentChar);

			enumSeq.getMembers().add(charLit);
		}

		return enumSeq;
	}

	public AQuoteLiteralExpCG consQuoteLiteral(String value)
	{
		AQuoteLiteralExpCG quoteLiteral = new AQuoteLiteralExpCG();
		quoteLiteral.setType(new AQuoteTypeCG());
		quoteLiteral.setValue(value);

		return quoteLiteral;
	}

	public AIntLiteralExpCG getDefaultIntValue()
	{
		return consIntLiteral(0L);
	}

	public ARealLiteralExpCG getDefaultRealValue()
	{
		return consRealLiteral(0.0);
	}

	public ABoolLiteralExpCG getDefaultBoolValue()
	{
		return consBoolLiteral(false);
	}

	public ACharLiteralExpCG getDefaultCharlValue()
	{
		return consCharLiteral('0');
	}

	public AStringLiteralExpCG getDefaultStringlValue()
	{
		return consStringLiteral("", true);
	}

	public ANullExpCG getDefaultClassValue()
	{
		return new ANullExpCG();
	}

	public boolean isAssigned(PExp exp)
	{
		SClassDefinition classDef = exp.getAncestor(SClassDefinition.class);

		if (classDef == null)
		{
			return false;
		}

		return exp.getAncestor(AInstanceVariableDefinition.class) != null
				|| exp.getAncestor(AValueDefinition.class) != null
				|| exp.getAncestor(AAssignmentDefinition.class) != null
				|| exp.getAncestor(AAssignmentStm.class) != null;
	}

	public AHeaderLetBeStCG consHeader(ASetMultipleBindCG binding,
			SExpCG suchThat)
	{
		AHeaderLetBeStCG header = new AHeaderLetBeStCG();

		header.setBinding(binding);
		header.setSuchThat(suchThat);

		return header;
	}

	public boolean existsOutsideOpOrFunc(PExp exp)
	{
		// The transformation of the 'and' and 'or' logical expressions also assumes that the
		// expressions exist within a statement. However, in case it does not, the transformation
		// is not performed. In this way, the  'and' and 'or' expressions can
		// still be used (say) in instance variable assignment.
		
		return exp.getAncestor(SOperationDefinition.class) == null
				&& exp.getAncestor(SFunctionDefinition.class) == null;
	}

	public SExpCG handleQuantifier(PExp node, List<PMultipleBind> bindings,
			PExp predicate, SQuantifierExpCG quantifier, IRInfo question,
			String nodeStr) throws AnalysisException
	{
		if (question.getExpAssistant().existsOutsideOpOrFunc(node))
		{
			question.addUnsupportedNode(node, String.format("Generation of a %s is only supported within operations/functions", nodeStr));
			return null;
		}

		LinkedList<ASetMultipleBindCG> bindingsCg = new LinkedList<ASetMultipleBindCG>();
		for (PMultipleBind multipleBind : bindings)
		{
			if (!(multipleBind instanceof ASetMultipleBind))
			{
				question.addUnsupportedNode(node, String.format("Generation of a %s is only supported for multiple set binds. Got: %s", nodeStr, multipleBind));
				return null;
			}

			SMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);

			if (!(multipleBindCg instanceof ASetMultipleBindCG))
			{
				question.addUnsupportedNode(node, String.format("Generation of a multiple set bind was expected to yield a ASetMultipleBindCG. Got: %s", multipleBindCg));
				return null;
			}

			bindingsCg.add((ASetMultipleBindCG) multipleBindCg);
		}

		PType type = node.getType();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG predicateCg = predicate.apply(question.getExpVisitor(), question);

		quantifier.setType(typeCg);
		quantifier.setBindList(bindingsCg);
		quantifier.setPredicate(predicateCg);

		return quantifier;
	}

	public void handleAlternativesCasesExp(IRInfo question, PExp exp,
			List<ACaseAlternative> cases, List<ACaseAltExpExpCG> casesCg)
			throws AnalysisException
	{
		for (ACaseAlternative alt : cases)
		{
			SExpCG altCg = alt.apply(question.getExpVisitor(), question);
			casesCg.add((ACaseAltExpExpCG) altCg);
		}

		PType expType = question.getTypeAssistant().resolve(exp.getType());
		
		if (expType instanceof AUnionType)
		{
			AUnionType unionType = ((AUnionType) expType).clone();
			question.getTcFactory().createAUnionTypeAssistant().expand(unionType);

			for (int i = 0; i < cases.size(); i++)
			{
				ACaseAlternative vdmCase = cases.get(i);
				ACaseAltExpExpCG cgCase = casesCg.get(i);

				PType patternType = question.getAssistantManager().getTypeAssistant().getType(question, unionType, vdmCase.getPattern());
				STypeCG patternTypeCg = patternType.apply(question.getTypeVisitor(), question);
				cgCase.setPatternType(patternTypeCg);
			}
		} else
		{
			STypeCG expTypeCg = expType.apply(question.getTypeVisitor(), question);

			for (ACaseAltExpExpCG altCg : casesCg)
			{
				altCg.setPatternType(expTypeCg.clone());
			}
		}
	}
	
	public boolean isLoopCondition(SExpCG exp)
	{
		INode node = exp.parent();
		
		while(node instanceof SExpCG)
		{
			node = node.parent();
		}
		
		return node instanceof AWhileStmCG || node instanceof AForLoopStmCG; 
		//The ForLoopStmCG is only used in the transformation process. It corresponds 
		//to the standard for loop in Java, e.g. for(int i = 0; i < 10; i++){...}
	}
	

	public SExpCG consGeneralIsExp(STypeCG typeCg, SExpCG expCg, STypeCG checkedTypeCg)
	{
		AGeneralIsExpCG generalIsExp = new AGeneralIsExpCG();
		generalIsExp = new AGeneralIsExpCG();
		generalIsExp.setType(typeCg);
		generalIsExp.setExp(expCg);
		generalIsExp.setCheckedType(checkedTypeCg);

		return generalIsExp;
	}
	
	public SExpCG consIsExpBasicType(AIsExp node, IRInfo question, String reason,
			PType checkedType, STypeCG typeCg, SExpCG expCg)
	{
		SIsExpCG basicIsExp = null;

		if (checkedType instanceof ABooleanBasicType)
		{
			basicIsExp = new ABoolIsExpCG();
		} else if (checkedType instanceof ANatNumericBasicType)
		{
			basicIsExp = new ANatIsExpCG();
		} else if (checkedType instanceof ANatOneNumericBasicType)
		{
			basicIsExp = new ANat1IsExpCG();
		} else if (checkedType instanceof AIntNumericBasicType)
		{
			basicIsExp = new AIntIsExpCG();
		} else if (checkedType instanceof ARationalNumericBasicType)
		{
			basicIsExp = new ARatIsExpCG();
		} else if (checkedType instanceof ARealNumericBasicType)
		{
			basicIsExp = new ARealIsExpCG();
		} else if (checkedType instanceof ACharBasicType)
		{
			basicIsExp = new ACharIsExpCG();
		} else if (checkedType instanceof ATokenBasicType)
		{
			basicIsExp = new ATokenIsExpCG();
		} else
		{
			// Could be a collection of any type
			question.addUnsupportedNode(node, reason);
			return null;
		}

		basicIsExp.setType(typeCg);
		basicIsExp.setExp(expCg);

		return basicIsExp;
	}
}
