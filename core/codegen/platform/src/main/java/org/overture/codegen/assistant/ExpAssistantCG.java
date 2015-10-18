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
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolIsExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACaseAltExpExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACharIsExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AGeneralIsExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntIsExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapSeqGetExpCG;
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
import org.overture.codegen.cgast.expressions.ATupleIsExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SIsExpCG;
import org.overture.codegen.cgast.expressions.SQuantifierExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AWhileStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AQuoteTypeCG;
import org.overture.codegen.cgast.types.ARatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.IRInfo;

public class ExpAssistantCG extends AssistantBase
{
	public ExpAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}
	
	public AIdentifierVarExpCG consIdVar(String name, STypeCG type)
	{
		AIdentifierVarExpCG var = new AIdentifierVarExpCG();
		var.setIsLambda(false);
		var.setIsLocal(true);
		var.setType(type);
		var.setName(name);

		return var;
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

		STypeCG typeCg = type != null ? type.apply(question.getTypeVisitor(), question) : null;
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
	
	public boolean isIntegerType(SExpCG exp)
	{
		STypeCG type = exp.getType();

		// Expressions like 1.0 are considered real literal expressions
		// of type NatOneNumericBasicType

		return (type instanceof ANat1NumericBasicTypeCG
				|| type instanceof ANatNumericBasicTypeCG || type instanceof AIntNumericBasicTypeCG)
				&& !(exp instanceof ARealLiteralExpCG);
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
	
	public AIntLiteralExpCG getDefaultNat1Value()
	{
		return consIntLiteral(1L);
	}

	public AIntLiteralExpCG getDefaultNatValue()
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

	public AHeaderLetBeStCG consHeader(SMultipleBindCG binding,
			SExpCG suchThat)
	{
		AHeaderLetBeStCG header = new AHeaderLetBeStCG();

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

	public SExpCG handleQuantifier(PExp node, List<PMultipleBind> bindings,
			PExp predicate, SQuantifierExpCG quantifier, IRInfo question,
			String nodeStr) throws AnalysisException
	{
		LinkedList<SMultipleBindCG> bindingsCg = new LinkedList<SMultipleBindCG>();
		for (PMultipleBind multipleBind : bindings)
		{
			SMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
			
			if(multipleBindCg != null)
			{
				bindingsCg.add(multipleBindCg);
			}
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
	
	public SExpCG consIsExp(SExpCG exp, STypeCG checkedType)
	{
		exp = exp.clone();
		checkedType = checkedType.clone();
		
		if (checkedType instanceof AUnionTypeCG)
		{
			return consGeneralIsExp(exp, checkedType);
		} else if (checkedType instanceof SBasicTypeCG)
		{
			return consIsExpBasicType(exp, checkedType);
		} else if (checkedType instanceof AQuoteTypeCG)
		{
			return consIsExpQuoteType(exp, (AQuoteTypeCG) checkedType);
		} else if (checkedType instanceof ATupleTypeCG)
		{
			return consTupleIsExp(exp, checkedType);
		} else if (checkedType instanceof ARecordTypeCG
				|| checkedType instanceof AClassTypeCG
				|| checkedType instanceof AStringTypeCG)
		{
			return consGeneralIsExp(exp, checkedType);
		}
		else
		{
			if(checkedType instanceof ASeqSeqTypeCG)
			{
				ASeqSeqTypeCG seqType = (ASeqSeqTypeCG) checkedType;
				
				if(seqType.getSeqOf() instanceof AUnknownTypeCG)
				{
					return consGeneralIsExp(exp, checkedType);
				}
			}
			else if(checkedType instanceof AMapMapTypeCG)
			{
				AMapMapTypeCG mapType = (AMapMapTypeCG) checkedType;
				
				if(mapType.getFrom() instanceof AUnknownTypeCG && mapType.getTo() instanceof AUnknownTypeCG)
				{
					return consGeneralIsExp(exp, checkedType);
				}
			}
			
			return null;
		}
	}

	public SExpCG consIsExpQuoteType(SExpCG exp, AQuoteTypeCG quoteType)
	{
		AQuoteLiteralExpCG lit = new AQuoteLiteralExpCG();
		lit.setType(quoteType);
		lit.setValue(quoteType.getValue());

		AEqualsBinaryExpCG equals = new AEqualsBinaryExpCG();
		equals.setType(new ABoolBasicTypeCG());
		equals.setLeft(exp);
		equals.setRight(lit);

		return equals;
	}
	
	public SExpCG consGeneralIsExp(SExpCG expCg, STypeCG checkedTypeCg)
	{
		AGeneralIsExpCG generalIsExp = new AGeneralIsExpCG();
		generalIsExp = new AGeneralIsExpCG();
		generalIsExp.setType(new ABoolBasicTypeCG());
		generalIsExp.setExp(expCg);
		generalIsExp.setCheckedType(checkedTypeCg);

		return generalIsExp;
	}
	
	public ATupleIsExpCG consTupleIsExp(SExpCG exp, STypeCG checkedType)
	{
		ATupleIsExpCG tupleIsExp = new ATupleIsExpCG();
		tupleIsExp.setType(new ABoolBasicTypeCG());
		tupleIsExp.setExp(exp);
		tupleIsExp.setCheckedType(checkedType);
		
		return tupleIsExp;
	}
	
	public SExpCG consIsExpBasicType(SExpCG expCg, STypeCG checkedType)
	{
		SIsExpCG basicIsExp = null;

		if (checkedType instanceof ABoolBasicTypeCG)
		{
			basicIsExp = new ABoolIsExpCG();
		} else if (checkedType instanceof ANatNumericBasicTypeCG)
		{
			basicIsExp = new ANatIsExpCG();
		} else if (checkedType instanceof ANat1NumericBasicTypeCG)
		{
			basicIsExp = new ANat1IsExpCG();
		} else if (checkedType instanceof AIntNumericBasicTypeCG)
		{
			basicIsExp = new AIntIsExpCG();
		} else if (checkedType instanceof ARatNumericBasicTypeCG)
		{
			basicIsExp = new ARatIsExpCG();
		} else if (checkedType instanceof ARealNumericBasicTypeCG)
		{
			basicIsExp = new ARealIsExpCG();
		} else if (checkedType instanceof ACharBasicTypeCG)
		{
			basicIsExp = new ACharIsExpCG();
		} else if (checkedType instanceof ATokenBasicTypeCG)
		{
			basicIsExp = new ATokenIsExpCG();
		}
		else
		{
			return null;
		}

		basicIsExp.setType(new ABoolBasicTypeCG());
		basicIsExp.setExp(expCg);

		return basicIsExp;
	}
	
	public SVarExpCG idStateDesignatorToExp(AIdentifierStateDesignatorCG node)
	{
		if(node.getExplicit())
		{
			AClassTypeCG classType = new AClassTypeCG();
			classType.setName(node.getClassName());
			
			AExplicitVarExpCG explicitVar = new AExplicitVarExpCG();
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
			AIdentifierVarExpCG idVar = consIdVar(node.getName(), node.getType().clone());
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
	
	public SExpCG findSubject(SExpCG next)
	{
		while (next instanceof AFieldExpCG || next instanceof AMapSeqGetExpCG
				|| next instanceof AApplyExpCG)
		{
			if (next instanceof AFieldExpCG)
			{
				next = ((AFieldExpCG) next).getObject();
			} else if (next instanceof AMapSeqGetExpCG)
			{
				next = ((AMapSeqGetExpCG) next).getCol();
			} else if (next instanceof AApplyExpCG)
			{
				next = ((AApplyExpCG) next).getRoot();
			}
		}

		return next;
	}
	
	public AUndefinedExpCG consUndefinedExp()
	{
		AUndefinedExpCG undefExp = new AUndefinedExpCG();
		undefExp.setType(new AUnknownTypeCG());
		
		return undefExp;
	}
	
	public ANullExpCG consNullExp()
	{
		ANullExpCG nullExp = new ANullExpCG();
		nullExp.setType(new AUnknownTypeCG());

		return nullExp;
	}
	
	public STypeCG handleMapType(SMapTypeBase node, IRInfo question, boolean isInjective) throws AnalysisException
	{
		PType from = node.getFrom();
		PType to = node.getTo();
		boolean empty = node.getEmpty();

		STypeCG fromCg = from.apply(question.getTypeVisitor(), question);
		STypeCG toCg = to.apply(question.getTypeVisitor(), question);

		AMapMapTypeCG mapType = new AMapMapTypeCG();
		mapType.setFrom(fromCg);
		mapType.setTo(toCg);
		mapType.setEmpty(empty);
		
		mapType.setInjective(isInjective);

		return mapType;
	}
	
	public boolean isUndefined(SExpCG exp)
	{
		if(exp instanceof ACastUnaryExpCG)
		{
			return isUndefined(((ACastUnaryExpCG) exp).getExp());
		}
		else if(exp instanceof AUndefinedExpCG)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
