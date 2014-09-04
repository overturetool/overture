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
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStateDesignatorCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AReverseUnaryExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
import org.overture.codegen.cgast.statements.ACasesStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AErrorStmCG;
import org.overture.codegen.cgast.statements.AForAllStmCG;
import org.overture.codegen.cgast.statements.AForIndexStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.statements.AStartStmCG;
import org.overture.codegen.cgast.statements.AWhileStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;

public class StmVisitorCG extends AbstractVisitorCG<IRInfo, SStmCG>
{
	public StmVisitorCG()
	{
	}

	@Override
	public SStmCG caseAErrorStm(AErrorStm node, IRInfo question)
			throws AnalysisException
	{
		return new AErrorStmCG();
	}

	@Override
	public SStmCG caseAAtomicStm(AAtomicStm node, IRInfo question)
			throws AnalysisException
	{
		LinkedList<AAssignmentStm> assignments = node.getAssignments();

		ABlockStmCG stmBlock = new ABlockStmCG();
		LinkedList<SStmCG> stmsCg = stmBlock.getStatements();

		for (AAssignmentStm assignment : assignments)
		{
			stmsCg.add(assignment.apply(question.getStmVisitor(), question));
		}

		return stmBlock;
	}

	@Override
	public SStmCG caseALetBeStStm(ALetBeStStm node, IRInfo question)
			throws AnalysisException
	{
		PMultipleBind multipleBind = node.getBind();

		if (!(multipleBind instanceof ASetMultipleBind))
		{
			question.addUnsupportedNode(node, "Generation of the let be st statement is only supported for a multiple set bind. Got: "
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

		PExp suchThat = node.getSuchThat();
		PStm stm = node.getStatement();

		SExpCG suchThatCg = suchThat != null ? suchThat.apply(question.getExpVisitor(), question)
				: null;
		SStmCG stmCg = stm.apply(question.getStmVisitor(), question);

		ALetBeStStmCG letBeSt = new ALetBeStStmCG();

		AHeaderLetBeStCG header = question.getExpAssistant().consHeader(multipleSetBindCg, suchThatCg);

		letBeSt.setHeader(header);
		letBeSt.setStatement(stmCg);

		return letBeSt;
	}

	@Override
	public SStmCG caseAWhileStm(AWhileStm node, IRInfo question)
			throws AnalysisException
	{
		PStm stm = node.getStatement();
		PExp exp = node.getExp();

		SStmCG bodyCg = stm.apply(question.getStmVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);

		AWhileStmCG whileStm = new AWhileStmCG();
		whileStm.setExp(expCg);
		whileStm.setBody(bodyCg);

		return whileStm;
	}

	@Override
	public SStmCG caseANotYetSpecifiedStm(ANotYetSpecifiedStm node,
			IRInfo question) throws AnalysisException
	{
		return new ANotImplementedStmCG();
	}

	@Override
	public SStmCG caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
			IRInfo question) throws AnalysisException
	{
		ABlockStmCG blockStm = new ABlockStmCG();

		LinkedList<AAssignmentDefinition> assignmentDefs = node.getAssignmentDefs();

		for (AAssignmentDefinition def : assignmentDefs)
		{
			// No protection against hidden definitions
			// dcl s : real := 1
			// dcl s : real := 2
			PType type = def.getType();
			String name = def.getName().getName();
			PExp exp = def.getExpression();

			STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

			AVarLocalDeclCG localDecl = new AVarLocalDeclCG();
			localDecl.setType(typeCg);

			AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
			idPattern.setName(name);

			localDecl.setPattern(idPattern);

			if (exp instanceof AUndefinedExp)
			{
				question.getDeclAssistant().setDefaultValue(localDecl, typeCg);
			} else
			{
				SExpCG expCg = exp.apply(question.getExpVisitor(), question);
				localDecl.setExp(expCg);
			}

			blockStm.getLocalDefs().add(localDecl);
		}

		LinkedList<PStm> stms = node.getStatements();

		for (PStm pStm : stms)
		{
			SStmCG stmCg = pStm.apply(question.getStmVisitor(), question);

			if (stmCg != null)
			{
				blockStm.getStatements().add(stmCg);
			}
		}

		return blockStm;
	}

	@Override
	public SStmCG caseAAssignmentStm(AAssignmentStm node, IRInfo question)
			throws AnalysisException
	{
		PStateDesignator target = node.getTarget();
		PExp exp = node.getExp();

		SStateDesignatorCG targetCg = target.apply(question.getStateDesignatorVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);

		AAssignmentStmCG assignment = new AAssignmentStmCG();
		assignment.setTarget(targetCg);
		assignment.setExp(expCg);

		return assignment;
	}

	@Override
	public SStmCG caseALetStm(ALetStm node, IRInfo question)
			throws AnalysisException
	{
		ALetDefStmCG localDefStm = new ALetDefStmCG();

		question.getDeclAssistant().setLocalDefs(node.getLocalDefs(), localDefStm.getLocalDefs(), question);

		SStmCG stm = node.getStatement().apply(question.getStmVisitor(), question);
		localDefStm.setStm(stm);

		return localDefStm;
	}

	@Override
	public SStmCG caseAReturnStm(AReturnStm node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExpression();

		AExplicitOperationDefinition operation = node.getAncestor(AExplicitOperationDefinition.class);

		if (operation != null && operation.getIsConstructor())
		{
			if (exp instanceof ASelfExp)
			{
				// The expression of the return statement points to 'null' since the OO AST
				// does not allow constructors to return references to explicitly
				// created types. Simply 'returning' in a constructor means returning
				// a reference for the object currently being created.
				return new AReturnStmCG();
			} else
			{
				throw new AnalysisExceptionCG("Unexpected expression returned by constructor: Values expliclty returned by constructors must be 'self'.", operation.getLocation());
			}
		}

		AReturnStmCG returnStm = new AReturnStmCG();

		if (exp != null)
		{
			SExpCG expCg = exp.apply(question.getExpVisitor(), question);
			returnStm.setExp(expCg);
		}

		return returnStm;
	}

	@Override
	public SStmCG caseACallStm(ACallStm node, IRInfo question)
			throws AnalysisException
	{
		PDefinition rootdef = node.getRootdef();
		LinkedList<PExp> args = node.getArgs();

		ACallStmCG callStm = new ACallStmCG();

		for (int i = 0; i < args.size(); i++)
		{
			PExp arg = args.get(i);
			SExpCG argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg == null)
			{
				question.addUnsupportedNode(node, "A Call statement is not supported for the argument: "
						+ arg);
				return null;
			}

			callStm.getArgs().add(argCg);
		}

		while (rootdef instanceof AInheritedDefinition)
		{
			rootdef = ((AInheritedDefinition) rootdef).getSuperdef();
		}

		if (rootdef instanceof AExplicitOperationDefinition)
		{
			AExplicitOperationDefinition op = (AExplicitOperationDefinition) rootdef;

			if (op.getIsConstructor())
			{
				String initName = question.getObjectInitializerCall(op);

				callStm.setType(new AVoidTypeCG());
				callStm.setClassType(null);
				callStm.setName(initName);

				return callStm;
			}
		}

		PType type = node.getType();
		ILexNameToken nameToken = node.getName();
		String name = nameToken.getName();
		boolean isStatic = question.getTcFactory().createPDefinitionAssistant().isStatic(rootdef);

		AClassTypeCG classType = null;

		if (nameToken != null && nameToken.getExplicit() && isStatic)
		{
			String className = nameToken.getModule();
			classType = new AClassTypeCG();
			classType.setName(className);
		}

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);

		callStm.setClassType(classType);
		callStm.setName(name);
		callStm.setType(typeCg);

		return callStm;
	}

	@Override
	public SStmCG caseACallObjectStm(ACallObjectStm node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PObjectDesignator objectDesignator = node.getDesignator();
		ILexNameToken field = node.getField();
		LinkedList<PExp> args = node.getArgs();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SObjectDesignatorCG objectDesignatorCg = objectDesignator.apply(question.getObjectDesignatorVisitor(), question);

		String classNameCg = null;

		if (node.getExplicit())
		{
			classNameCg = field.getModule();
		}

		String fieldNameCg = field.getName();

		ACallObjectStmCG callObject = new ACallObjectStmCG();
		callObject.setType(typeCg);
		callObject.setDesignator(objectDesignatorCg);
		callObject.setClassName(classNameCg);
		callObject.setFieldName(fieldNameCg);

		for (int i = 0; i < args.size(); i++)
		{
			PExp arg = args.get(i);
			SExpCG argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg == null)
			{
				question.addUnsupportedNode(node, "A Call object statement is not supported for the argument: "
						+ arg);
				return null;
			}

			callObject.getArgs().add(argCg);
		}

		return callObject;
	}

	@Override
	public SStmCG caseAElseIfStm(AElseIfStm node, IRInfo question)
			throws AnalysisException
	{
		// Don't visit it but create it directly if needed in the ifStm in order to avoid casting
		return null;
	}

	@Override
	public SStmCG caseACasesStm(ACasesStm node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		PStm others = node.getOthers();
		LinkedList<ACaseAlternativeStm> cases = node.getCases();

		SExpCG expCg = exp.apply(question.getExpVisitor(), question);
		SStmCG othersCg = others != null ? others.apply(question.getStmVisitor(), question)
				: null;

		ACasesStmCG casesStmCg = new ACasesStmCG();
		casesStmCg.setExp(expCg);
		casesStmCg.setOthers(othersCg);
		;

		question.getStmAssistant().handleAlternativesCasesStm(question, exp, cases, casesStmCg.getCases());

		return casesStmCg;
	}

	@Override
	public SStmCG caseACaseAlternativeStm(ACaseAlternativeStm node,
			IRInfo question) throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		PStm result = node.getResult();

		SPatternCG patternCg = pattern.apply(question.getPatternVisitor(), question);
		SStmCG resultCg = result.apply(question.getStmVisitor(), question);

		ACaseAltStmStmCG caseCg = new ACaseAltStmStmCG();
		caseCg.setPattern(patternCg);
		caseCg.setResult(resultCg);

		return caseCg;
	}

	@Override
	public SStmCG caseAIfStm(AIfStm node, IRInfo question)
			throws AnalysisException
	{
		SExpCG ifExp = node.getIfExp().apply(question.getExpVisitor(), question);
		SStmCG thenStm = node.getThenStm().apply(question.getStmVisitor(), question);

		AIfStmCG ifStm = new AIfStmCG();

		ifStm.setIfExp(ifExp);
		ifStm.setThenStm(thenStm);

		LinkedList<AElseIfStm> elseIfs = node.getElseIf();

		for (AElseIfStm stm : elseIfs)
		{
			ifExp = stm.getElseIf().apply(question.getExpVisitor(), question);
			thenStm = stm.getThenStm().apply(question.getStmVisitor(), question);

			AElseIfStmCG elseIfStm = new AElseIfStmCG();
			elseIfStm.setElseIf(ifExp);
			elseIfStm.setThenStm(thenStm);

			ifStm.getElseIf().add(elseIfStm);
		}

		if (node.getElseStm() != null)
		{
			SStmCG elseStm = node.getElseStm().apply(question.getStmVisitor(), question);
			ifStm.setElseStm(elseStm);
		}

		return ifStm;

	}

	@Override
	public SStmCG caseASkipStm(ASkipStm node, IRInfo question)
			throws AnalysisException
	{
		return new ASkipStmCG();
	}

	@Override
	public SStmCG caseASubclassResponsibilityStm(
			ASubclassResponsibilityStm node, IRInfo question)
			throws AnalysisException
	{
		return null;// Indicates an abstract body
	}

	@Override
	public SStmCG caseAForIndexStm(AForIndexStm node, IRInfo question)
			throws AnalysisException
	{
		ILexNameToken var = node.getVar();
		PExp from = node.getFrom();
		PExp to = node.getTo();
		PExp by = node.getBy();
		PStm stm = node.getStatement();

		String varCg = var.getName();
		SExpCG fromCg = from.apply(question.getExpVisitor(), question);
		SExpCG toCg = to.apply(question.getExpVisitor(), question);
		SExpCG byCg = by != null ? by.apply(question.getExpVisitor(), question)
				: null;
		SStmCG bodyCg = stm.apply(question.getStmVisitor(), question);

		AForIndexStmCG forStm = new AForIndexStmCG();
		forStm.setVar(varCg);
		forStm.setFrom(fromCg);
		forStm.setTo(toCg);
		forStm.setBy(byCg);
		forStm.setBody(bodyCg);

		return forStm;
	}

	@Override
	public SStmCG caseAForAllStm(AForAllStm node, IRInfo question)
			throws AnalysisException
	{
		//Example: for all x in set {1,2,3} do skip;
		PPattern pattern = node.getPattern();

		//TODO: Missing case for generation of patterns
		if (!(pattern instanceof AIdentifierPattern))
		{
			question.addUnsupportedNode(node, "Generation of the for all statement only supports identifier patterns. Got: " + pattern);
			return null; // This is the only pattern supported by this loop construct
		}

		AIdentifierPattern identifier = (AIdentifierPattern) pattern;
		PExp set = node.getSet();
		PStm body = node.getStatement();

		String var = identifier.getName().getName();
		SExpCG setExpCg = set.apply(question.getExpVisitor(), question);
		SStmCG bodyCg = body.apply(question.getStmVisitor(), question);

		AForAllStmCG forAll = new AForAllStmCG();
		forAll.setVar(var);
		forAll.setExp(setExpCg);
		forAll.setBody(bodyCg);

		return forAll;
	}

	@Override
	public SStmCG caseAForPatternBindStm(AForPatternBindStm node,
			IRInfo question) throws AnalysisException
	{
		ADefPatternBind patternBind = node.getPatternBind();

		PPattern pattern = patternBind.getPattern();

		//TODO: Missing case for generation of patterns
		if (!(pattern instanceof AIdentifierPattern))
		{
			question.addUnsupportedNode(node, "Generation of the for pattern bind statement only supports identifier patterns. Got: " + pattern);
			return null; // This is the only pattern supported by this loop construct
		}

		AIdentifierPattern identifier = (AIdentifierPattern) pattern;
		Boolean reverse = node.getReverse();
		PExp exp = node.getExp();
		PStm stm = node.getStatement();

		String var = identifier.getName().getName();
		SExpCG seqExpCg = exp.apply(question.getExpVisitor(), question);
		SStmCG stmCg = stm.apply(question.getStmVisitor(), question);

		AForAllStmCG forAll = new AForAllStmCG();
		forAll.setVar(var);
		forAll.setBody(stmCg);

		if (reverse != null && reverse)
		{
			AReverseUnaryExpCG reversedExp = new AReverseUnaryExpCG();
			reversedExp.setType(seqExpCg.getType().clone());
			reversedExp.setExp(seqExpCg);
			forAll.setExp(reversedExp);
		} else
		{
			forAll.setExp(seqExpCg);
		}

		return forAll;
	}
	
	@Override
	public SStmCG caseAStartStm(AStartStm node, IRInfo question)
			throws AnalysisException
	{
		
		PExp exp = node.getObj();
		
		SExpCG expCG = exp.apply(question.getExpVisitor(), question);
		
		AStartStmCG thread = new AStartStmCG();
		
		//System.out.print(expCG);
		
		thread.setExp(expCG);
				
		return thread;
		
	}
}
