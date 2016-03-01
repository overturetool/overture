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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapTypeBase;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ABoolIsExpIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.ACaseAltExpExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.ACharIsExpIR;
import org.overture.codegen.ir.expressions.ACharLiteralExpIR;
import org.overture.codegen.ir.expressions.AEnumSeqExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AGeneralIsExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntIsExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.AIsolationUnaryExpIR;
import org.overture.codegen.ir.expressions.AMapSeqGetExpIR;
import org.overture.codegen.ir.expressions.ANat1IsExpIR;
import org.overture.codegen.ir.expressions.ANatIsExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.expressions.ANullExpIR;
import org.overture.codegen.ir.expressions.AQuoteLiteralExpIR;
import org.overture.codegen.ir.expressions.ARatIsExpIR;
import org.overture.codegen.ir.expressions.ARealIsExpIR;
import org.overture.codegen.ir.expressions.ARealLiteralExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.expressions.ATokenIsExpIR;
import org.overture.codegen.ir.expressions.ATupleIsExpIR;
import org.overture.codegen.ir.expressions.AUndefinedExpIR;
import org.overture.codegen.ir.expressions.SBinaryExpIR;
import org.overture.codegen.ir.expressions.SIsExpIR;
import org.overture.codegen.ir.expressions.SQuantifierExpIR;
import org.overture.codegen.ir.expressions.SUnaryExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.AForLoopStmIR;
import org.overture.codegen.ir.statements.AIdentifierStateDesignatorIR;
import org.overture.codegen.ir.statements.AWhileStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMapMapTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;
import org.overture.codegen.ir.types.ATupleTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.types.SBasicTypeIR;
import org.overture.codegen.ir.utils.AHeaderLetBeStIR;
import org.overture.codegen.ir.IRInfo;

public class ExpAssistantIR extends AssistantBase
{
	public ExpAssistantIR(AssistantManager assistantManager)
	{
		super(assistantManager);
	}
	
	public AIdentifierVarExpIR consIdVar(String name, STypeIR type)
	{
		AIdentifierVarExpIR var = new AIdentifierVarExpIR();
		var.setIsLambda(false);
		var.setIsLocal(true);
		var.setType(type);
		var.setName(name);

		return var;
	}

	public SExpIR isolateExpression(SExpIR exp)
	{
		AIsolationUnaryExpIR isolationExp = new AIsolationUnaryExpIR();
		isolationExp.setExp(exp);
		isolationExp.setType(exp.getType().clone());
		return isolationExp;
	}

	public ANotUnaryExpIR negate(SExpIR exp)
	{
		ANotUnaryExpIR negated = new ANotUnaryExpIR();
		negated.setType(exp.getType().clone());
		negated.setExp(exp);

		return negated;
	}

	public SExpIR handleUnaryExp(SUnaryExp vdmExp, SUnaryExpIR codeGenExp,
			IRInfo question) throws AnalysisException
	{
		SExpIR expCg = vdmExp.getExp().apply(question.getExpVisitor(), question);
		STypeIR typeCg = vdmExp.getType().apply(question.getTypeVisitor(), question);

		codeGenExp.setType(typeCg);
		codeGenExp.setExp(expCg);

		return codeGenExp;
	}

	public SExpIR handleBinaryExp(SBinaryExp vdmExp, SBinaryExpIR codeGenExp,
			IRInfo question) throws AnalysisException
	{
		PType type = vdmExp.getType();

		STypeIR typeCg = type != null ? type.apply(question.getTypeVisitor(), question) : null;
		codeGenExp.setType(typeCg);

		PExp vdmExpLeft = vdmExp.getLeft();
		PExp vdmExpRight = vdmExp.getRight();

		SExpIR leftExpCg = vdmExpLeft.apply(question.getExpVisitor(), question);
		SExpIR rightExpCg = vdmExpRight.apply(question.getExpVisitor(), question);

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
	
	public boolean isIntegerType(SExpIR exp)
	{
		STypeIR type = exp.getType();

		// Expressions like 1.0 are considered real literal expressions
		// of type NatOneNumericBasicType

		return (type instanceof ANat1NumericBasicTypeIR
				|| type instanceof ANatNumericBasicTypeIR || type instanceof AIntNumericBasicTypeIR)
				&& !(exp instanceof ARealLiteralExpIR);
	}

	public ABoolLiteralExpIR consBoolLiteral(boolean val)
	{
		ABoolLiteralExpIR boolLiteral = new ABoolLiteralExpIR();
		boolLiteral.setType(new ABoolBasicTypeIR());
		boolLiteral.setValue(val);

		return boolLiteral;
	}

	public AIntLiteralExpIR consIntLiteral(long value)
	{
		AIntLiteralExpIR intLiteral = new AIntLiteralExpIR();
		intLiteral.setType(new AIntNumericBasicTypeIR());
		intLiteral.setValue(value);

		return intLiteral;
	}

	public ARealLiteralExpIR consRealLiteral(double value)
	{
		ARealLiteralExpIR realLiteral = new ARealLiteralExpIR();
		realLiteral.setType(new ARealNumericBasicTypeIR());
		realLiteral.setValue(value);

		return realLiteral;
	}

	public ACharLiteralExpIR consCharLiteral(char value)
	{
		ACharLiteralExpIR charLiteral = new ACharLiteralExpIR();
		charLiteral.setType(new ACharBasicTypeIR());
		charLiteral.setValue(value);

		return charLiteral;
	}

	public AStringLiteralExpIR consStringLiteral(String value, boolean isNull)
	{
		AStringLiteralExpIR stringLiteral = new AStringLiteralExpIR();

		stringLiteral.setType(new AStringTypeIR());
		stringLiteral.setIsNull(isNull);
		stringLiteral.setValue(value);

		return stringLiteral;
	}

	public SExpIR consCharSequence(STypeIR seqType, String value)
	{
		AEnumSeqExpIR enumSeq = new AEnumSeqExpIR();

		enumSeq.setType(seqType);

		for (int i = 0; i < value.length(); i++)
		{
			char currentChar = value.charAt(i);
			ACharLiteralExpIR charLit = new ACharLiteralExpIR();
			charLit.setType(new ACharBasicTypeIR());
			charLit.setValue(currentChar);

			enumSeq.getMembers().add(charLit);
		}

		return enumSeq;
	}

	public AQuoteLiteralExpIR consQuoteLiteral(String value)
	{
		AQuoteLiteralExpIR quoteLiteral = new AQuoteLiteralExpIR();
		AQuoteTypeIR type = new AQuoteTypeIR();
		type.setValue(value);
		quoteLiteral.setType(type);
		quoteLiteral.setValue(value);

		return quoteLiteral;
	}

	public AIntLiteralExpIR getDefaultIntValue()
	{
		return consIntLiteral(0L);
	}
	
	public AIntLiteralExpIR getDefaultNat1Value()
	{
		return consIntLiteral(1L);
	}

	public AIntLiteralExpIR getDefaultNatValue()
	{
		return consIntLiteral(0L);
	}
	
	public ARealLiteralExpIR getDefaultRealValue()
	{
		return consRealLiteral(0.0);
	}

	public ABoolLiteralExpIR getDefaultBoolValue()
	{
		return consBoolLiteral(false);
	}

	public ACharLiteralExpIR getDefaultCharlValue()
	{
		return consCharLiteral('0');
	}

	public AStringLiteralExpIR getDefaultStringlValue()
	{
		return consStringLiteral("", true);
	}

	public boolean isAssigned(PExp exp)
	{
		org.overture.ast.node.INode parent = exp.parent();

		if (parent == null)
		{
			return false;
		}

		Set<org.overture.ast.node.INode> visitedNodes = new HashSet<>();
		visitedNodes.add(parent);

		do
		{
			if (parent instanceof AInstanceVariableDefinition
					| parent instanceof AValueDefinition
					| parent instanceof AAssignmentDefinition
					| parent instanceof AAssignmentStm)
			{
				return true;
			}

			if (parent instanceof ALambdaExp)
			{
				return false;
			}

			parent = parent.parent();

			if (parent != null)
			{
				visitedNodes.add(parent);
			}
			
		} while (parent != null && !visitedNodes.contains(parent));
		
		return false;
	}

	public AHeaderLetBeStIR consHeader(SMultipleBindIR binding,
			SExpIR suchThat)
	{
		AHeaderLetBeStIR header = new AHeaderLetBeStIR();

		header.setBinding(binding);
		header.setSuchThat(suchThat);

		return header;
	}
	
	public boolean appearsInModuleStateInv(org.overture.ast.node.INode node)
	{
		AStateDefinition stateDef = node.getAncestor(AStateDefinition.class);
		if (stateDef != null)
		{
			LinkedList<org.overture.ast.node.INode> ancestors = new LinkedList<>();
			org.overture.ast.node.INode next = node;

			do
			{
				ancestors.add(next);
				next = node.parent();
			} while (!(next instanceof AStateDefinition)
					&& !ancestors.contains(next));

			if (ancestors.getLast() == stateDef.getInvExpression())
			{
				return true;
			}
		}
		return false;
	}

	public boolean outsideImperativeContext(org.overture.ast.node.INode node)
	{
		// The transformation of the 'and' and 'or' logical expressions also assumes that the
		// expressions exist within a statement. However, in case it does not, the transformation
		// is not performed. In this way, the  'and' and 'or' expressions can
		// still be used (say) in instance variable assignment.
		
		return node.getAncestor(SOperationDefinition.class) == null
				&& node.getAncestor(SFunctionDefinition.class) == null
				&& node.getAncestor(ANamedTraceDefinition.class) == null
				&& node.getAncestor(ATypeDefinition.class) == null
				&& node.getAncestor(AClassInvariantDefinition.class) == null;
	}

	public SExpIR handleQuantifier(PExp node, List<PMultipleBind> bindings,
			PExp predicate, SQuantifierExpIR quantifier, IRInfo question,
			String nodeStr) throws AnalysisException
	{
		LinkedList<SMultipleBindIR> bindingsCg = new LinkedList<SMultipleBindIR>();
		for (PMultipleBind multipleBind : bindings)
		{
			SMultipleBindIR multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
			
			if(multipleBindCg != null)
			{
				bindingsCg.add(multipleBindCg);
			}
		}

		PType type = node.getType();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR predicateCg = predicate.apply(question.getExpVisitor(), question);

		quantifier.setType(typeCg);
		quantifier.setBindList(bindingsCg);
		quantifier.setPredicate(predicateCg);

		return quantifier;
	}

	public void handleAlternativesCasesExp(IRInfo question, PExp exp,
			List<ACaseAlternative> cases, List<ACaseAltExpExpIR> casesCg)
			throws AnalysisException
	{
		for (ACaseAlternative alt : cases)
		{
			SExpIR altCg = alt.apply(question.getExpVisitor(), question);
			casesCg.add((ACaseAltExpExpIR) altCg);
		}

		PType expType = question.getTypeAssistant().resolve(exp.getType());
		
		if (expType instanceof AUnionType)
		{
			AUnionType unionType = ((AUnionType) expType).clone();
			question.getTcFactory().createAUnionTypeAssistant().expand(unionType);

			for (int i = 0; i < cases.size(); i++)
			{
				ACaseAlternative vdmCase = cases.get(i);
				ACaseAltExpExpIR cgCase = casesCg.get(i);

				PType patternType = question.getAssistantManager().getTypeAssistant().getType(question, unionType, vdmCase.getPattern());
				STypeIR patternTypeCg = patternType.apply(question.getTypeVisitor(), question);
				cgCase.setPatternType(patternTypeCg);
			}
		} else
		{
			STypeIR expTypeCg = expType.apply(question.getTypeVisitor(), question);

			for (ACaseAltExpExpIR altCg : casesCg)
			{
				altCg.setPatternType(expTypeCg.clone());
			}
		}
	}
	
	public boolean isLoopCondition(SExpIR exp)
	{
		INode node = exp.parent();
		
		while(node instanceof SExpIR)
		{
			node = node.parent();
		}
		
		return node instanceof AWhileStmIR || node instanceof AForLoopStmIR; 
		//The ForLoopStmIR is only used in the transformation process. It corresponds 
		//to the standard for loop in Java, e.g. for(int i = 0; i < 10; i++){...}
	}
	
	public SExpIR consIsExp(SExpIR exp, STypeIR checkedType)
	{
		exp = exp.clone();
		checkedType = checkedType.clone();
		
		if (checkedType instanceof AUnionTypeIR)
		{
			return consGeneralIsExp(exp, checkedType);
		} else if (checkedType instanceof SBasicTypeIR)
		{
			return consIsExpBasicType(exp, checkedType);
		} else if (checkedType instanceof AQuoteTypeIR)
		{
			return consIsExpQuoteType(exp, (AQuoteTypeIR) checkedType);
		} else if (checkedType instanceof ATupleTypeIR)
		{
			return consTupleIsExp(exp, checkedType);
		} else if (checkedType instanceof ARecordTypeIR
				|| checkedType instanceof AClassTypeIR
				|| checkedType instanceof AStringTypeIR)
		{
			return consGeneralIsExp(exp, checkedType);
		}
		else
		{
			if(checkedType instanceof ASeqSeqTypeIR)
			{
				ASeqSeqTypeIR seqType = (ASeqSeqTypeIR) checkedType;
				
				if(seqType.getSeqOf() instanceof AUnknownTypeIR)
				{
					return consGeneralIsExp(exp, checkedType);
				}
			}
			else if(checkedType instanceof AMapMapTypeIR)
			{
				AMapMapTypeIR mapType = (AMapMapTypeIR) checkedType;
				
				if(mapType.getFrom() instanceof AUnknownTypeIR && mapType.getTo() instanceof AUnknownTypeIR)
				{
					return consGeneralIsExp(exp, checkedType);
				}
			}
			
			return null;
		}
	}

	public SExpIR consIsExpQuoteType(SExpIR exp, AQuoteTypeIR quoteType)
	{
		AQuoteLiteralExpIR lit = new AQuoteLiteralExpIR();
		lit.setType(quoteType);
		lit.setValue(quoteType.getValue());

		AEqualsBinaryExpIR equals = new AEqualsBinaryExpIR();
		equals.setType(new ABoolBasicTypeIR());
		equals.setLeft(exp);
		equals.setRight(lit);

		return equals;
	}
	
	public SExpIR consGeneralIsExp(SExpIR expCg, STypeIR checkedTypeCg)
	{
		AGeneralIsExpIR generalIsExp = new AGeneralIsExpIR();
		generalIsExp = new AGeneralIsExpIR();
		generalIsExp.setType(new ABoolBasicTypeIR());
		generalIsExp.setExp(expCg);
		generalIsExp.setCheckedType(checkedTypeCg);

		return generalIsExp;
	}
	
	public ATupleIsExpIR consTupleIsExp(SExpIR exp, STypeIR checkedType)
	{
		ATupleIsExpIR tupleIsExp = new ATupleIsExpIR();
		tupleIsExp.setType(new ABoolBasicTypeIR());
		tupleIsExp.setExp(exp);
		tupleIsExp.setCheckedType(checkedType);
		
		return tupleIsExp;
	}
	
	public SExpIR consIsExpBasicType(SExpIR expCg, STypeIR checkedType)
	{
		SIsExpIR basicIsExp = null;

		if (checkedType instanceof ABoolBasicTypeIR)
		{
			basicIsExp = new ABoolIsExpIR();
		} else if (checkedType instanceof ANatNumericBasicTypeIR)
		{
			basicIsExp = new ANatIsExpIR();
		} else if (checkedType instanceof ANat1NumericBasicTypeIR)
		{
			basicIsExp = new ANat1IsExpIR();
		} else if (checkedType instanceof AIntNumericBasicTypeIR)
		{
			basicIsExp = new AIntIsExpIR();
		} else if (checkedType instanceof ARatNumericBasicTypeIR)
		{
			basicIsExp = new ARatIsExpIR();
		} else if (checkedType instanceof ARealNumericBasicTypeIR)
		{
			basicIsExp = new ARealIsExpIR();
		} else if (checkedType instanceof ACharBasicTypeIR)
		{
			basicIsExp = new ACharIsExpIR();
		} else if (checkedType instanceof ATokenBasicTypeIR)
		{
			basicIsExp = new ATokenIsExpIR();
		}
		else
		{
			return null;
		}

		basicIsExp.setType(new ABoolBasicTypeIR());
		basicIsExp.setExp(expCg);

		return basicIsExp;
	}
	
	public SVarExpIR idStateDesignatorToExp(AIdentifierStateDesignatorIR node)
	{
		if(node.getExplicit())
		{
			AClassTypeIR classType = new AClassTypeIR();
			classType.setName(node.getClassName());
			
			AExplicitVarExpIR explicitVar = new AExplicitVarExpIR();
			explicitVar.setClassType(classType);
			explicitVar.setIsLambda(false);
			explicitVar.setIsLocal(node.getIsLocal());
			explicitVar.setName(node.getName());
			explicitVar.setSourceNode(node.getSourceNode());
			explicitVar.setTag(node.getTag());
			explicitVar.setType(node.getType().clone());
			
			return explicitVar;
		}
		else
		{
			AIdentifierVarExpIR idVar = consIdVar(node.getName(), node.getType().clone());
			idVar.setTag(node.getTag());
			idVar.setSourceNode(node.getSourceNode());
			idVar.setIsLocal(node.getIsLocal());
			
			return idVar;
		}
	}
	
	public boolean isOld(String name)
	{
		return name != null && name.startsWith("_");
	}
	
	public String oldNameToCurrentName(String oldName)
	{
		if(oldName != null && oldName.startsWith("_"))
		{
			return oldName.substring(1);
		}
		else
		{
			return oldName;
		}
	}
	
	public boolean isResult(String name)
	{
		return name != null && name.equals("RESULT");
	}
	
	public SExpIR findSubject(SExpIR next)
	{
		while (next instanceof AFieldExpIR || next instanceof AMapSeqGetExpIR
				|| next instanceof AApplyExpIR)
		{
			if (next instanceof AFieldExpIR)
			{
				next = ((AFieldExpIR) next).getObject();
			} else if (next instanceof AMapSeqGetExpIR)
			{
				next = ((AMapSeqGetExpIR) next).getCol();
			} else if (next instanceof AApplyExpIR)
			{
				next = ((AApplyExpIR) next).getRoot();
			}
		}

		return next;
	}
	
	public AUndefinedExpIR consUndefinedExp()
	{
		AUndefinedExpIR undefExp = new AUndefinedExpIR();
		undefExp.setType(new AUnknownTypeIR());
		
		return undefExp;
	}
	
	public ANullExpIR consNullExp()
	{
		ANullExpIR nullExp = new ANullExpIR();
		nullExp.setType(new AUnknownTypeIR());

		return nullExp;
	}
	
	public STypeIR handleMapType(SMapTypeBase node, IRInfo question, boolean isInjective) throws AnalysisException
	{
		PType from = node.getFrom();
		PType to = node.getTo();
		boolean empty = node.getEmpty();

		STypeIR fromCg = from.apply(question.getTypeVisitor(), question);
		STypeIR toCg = to.apply(question.getTypeVisitor(), question);

		AMapMapTypeIR mapType = new AMapMapTypeIR();
		mapType.setFrom(fromCg);
		mapType.setTo(toCg);
		mapType.setEmpty(empty);
		
		mapType.setInjective(isInjective);

		return mapType;
	}
	
	public boolean isUndefined(SExpIR exp)
	{
		if(exp instanceof ACastUnaryExpIR)
		{
			return isUndefined(((ACastUnaryExpIR) exp).getExp());
		}
		else if(exp instanceof AUndefinedExpIR)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
