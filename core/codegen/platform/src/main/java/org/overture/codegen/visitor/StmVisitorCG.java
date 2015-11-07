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
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ASelfExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AErrorStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStateDesignatorCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AReverseUnaryExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
import org.overture.codegen.cgast.statements.ACasesStmCG;
import org.overture.codegen.cgast.statements.ACyclesStmCG;
import org.overture.codegen.cgast.statements.ADurationStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AErrorStmCG;
import org.overture.codegen.cgast.statements.AForAllStmCG;
import org.overture.codegen.cgast.statements.AForIndexStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.APeriodicStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.statements.AStartStmCG;
import org.overture.codegen.cgast.statements.AStartlistStmCG;
import org.overture.codegen.cgast.statements.ASuperCallStmCG;
import org.overture.codegen.cgast.statements.AWhileStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.config.Settings;

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
	public SStmCG caseAClassInvariantStm(AClassInvariantStm node,
			IRInfo question) throws AnalysisException
	{
		List<PExp> exps = new LinkedList<PExp>();
		
		for (PDefinition d : node.getInvDefs())
		{
			if(!(d instanceof AClassInvariantDefinition))
			{
				Logger.getLog().printErrorln("Expected class invariant definition in '" + this.getClass().getName() + "'. Got: " + d);
				return null;
			}
			
			AClassInvariantDefinition invDef = (AClassInvariantDefinition) d;
			exps.add(invDef.getExpression());
		}
		
		AReturnStmCG returnStmCg = new AReturnStmCG();
		
		if(exps.isEmpty())
		{
			// Should not really be necessary
			returnStmCg.setExp(question.getExpAssistant().consBoolLiteral(true));
		}
		else if(exps.size() == 1)
		{
			SExpCG expCg = exps.get(0).apply(question.getExpVisitor(), question);
			returnStmCg.setExp(expCg);
		}
		else
		{
			// We have more than one expressions from which we will build an 'and chain'
			AAndBoolBinaryExpCG andExpTopCg = new AAndBoolBinaryExpCG();
			andExpTopCg.setType(new ABoolBasicTypeCG());
			andExpTopCg.setLeft(exps.get(0).apply(question.getExpVisitor(), question));
			
			AAndBoolBinaryExpCG previousAndExpCg = andExpTopCg;
			
			// The remaining ones except the last
			for(int i = 1; i < exps.size() - 1; i++)
			{
				SExpCG nextExpCg = exps.get(i).apply(question.getExpVisitor(), question);
				
				AAndBoolBinaryExpCG nextAndExpCg = new AAndBoolBinaryExpCG();
				nextAndExpCg.setType(new ABoolBasicTypeCG());
				nextAndExpCg.setLeft(nextExpCg);
				
				previousAndExpCg.setRight(nextAndExpCg);
				previousAndExpCg = nextAndExpCg;
			}
			
			previousAndExpCg.setRight(exps.get(exps.size() - 1).apply(question.getExpVisitor(), question));
			
			returnStmCg.setExp(andExpTopCg);
		}

		return returnStmCg;
	}
	
	@Override
	public SStmCG caseAPeriodicStm(APeriodicStm node, IRInfo question)
			throws AnalysisException
	{
		String opName = node.getOpname().getName();
		
		APeriodicStmCG periodicStmCg = new APeriodicStmCG();
		periodicStmCg.setOpname(opName);
		
		for(PExp exp : node.getArgs())
		{
			SExpCG expCg = exp.apply(question.getExpVisitor(), question);
			
			if(expCg != null)
			{
				periodicStmCg.getArgs().add(expCg);
			}
			else
			{
				return null;
			}
		}
		
		return periodicStmCg;
	}

	@Override
	public SStmCG caseAAtomicStm(AAtomicStm node, IRInfo question)
			throws AnalysisException
	{
		AAtomicStmCG atomicBlock = new AAtomicStmCG();
		
		for (AAssignmentStm assignment : node.getAssignments())
		{
			SStmCG stmCg = assignment.apply(question.getStmVisitor(), question);
			
			if(stmCg != null)
			{
				atomicBlock.getStatements().add(stmCg);
			}
			else
			{
				return null;
			}
		}

		return atomicBlock;
	}
	
	@Override
	public SStmCG caseACyclesStm(ACyclesStm node, IRInfo question) throws AnalysisException
	{
		PExp cycles = node.getCycles();
		PStm stm = node.getStatement();
		
		SExpCG cyclesCg = cycles.apply(question.getExpVisitor(), question);
		SStmCG stmCg = stm.apply(question.getStmVisitor(), question);
		
		ACyclesStmCG cycStm = new ACyclesStmCG();
		cycStm.setCycles(cyclesCg);
		cycStm.setStm(stmCg);
		
		return cycStm;
	}
	
	@Override
	public SStmCG caseADurationStm(ADurationStm node, IRInfo question) throws AnalysisException
	{
		PExp duration = node.getDuration();
		PStm stm = node.getStatement();
		
		SExpCG durationCg = duration.apply(question.getExpVisitor(), question);
		SStmCG stmCg = stm.apply(question.getStmVisitor(), question);
		
		ADurationStmCG durStm = new ADurationStmCG();
		durStm.setDuration(durationCg);
		durStm.setStm(stmCg);;
		
		return durStm;
	}

	@Override
	public SStmCG caseALetBeStStm(ALetBeStStm node, IRInfo question)
			throws AnalysisException
	{
		PMultipleBind multipleBind = node.getBind();
		SMultipleBindCG multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
		
		PExp suchThat = node.getSuchThat();
		PStm stm = node.getStatement();

		SExpCG suchThatCg = suchThat != null ? suchThat.apply(question.getExpVisitor(), question)
				: null;
		SStmCG stmCg = stm.apply(question.getStmVisitor(), question);

		ALetBeStStmCG letBeSt = new ALetBeStStmCG();

		AHeaderLetBeStCG header = question.getExpAssistant().consHeader(multipleBindCg, suchThatCg);

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
		blockStm.setScoped(question.getStmAssistant().isScoped(node));

		LinkedList<AAssignmentDefinition> assignmentDefs = node.getAssignmentDefs();

		for (AAssignmentDefinition def : assignmentDefs)
		{
			PType type = def.getType();
			String name = def.getName().getName();
			PExp exp = def.getExpression();

			STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
			AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
			idPattern.setName(name);
			SExpCG expCg = exp.apply(question.getExpVisitor(), question);
			
			AVarDeclCG localDecl = question.getDeclAssistant().consLocalVarDecl(def, typeCg, idPattern, expCg);
			
			if (expCg instanceof AUndefinedExpCG)
			{
				question.getDeclAssistant().setDefaultValue(localDecl, typeCg);
			} else
			{
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
			else
			{
				return null;
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
		ABlockStmCG block = new ABlockStmCG();
		block.setScoped(question.getStmAssistant().isScoped(node));
		
		question.getDeclAssistant().setLocalDefs(node.getLocalDefs(), block.getLocalDefs(), question);

		SStmCG stm = node.getStatement().apply(question.getStmVisitor(), question);
		
		if (stm != null)
		{
			block.getStatements().add(stm);
		}

		return block;
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
				question.addUnsupportedNode(operation, "Unexpected expression returned by constructor: Values expliclty returned by constructors must be 'self'.");
				return null;
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

		List<SExpCG> argsCg = new LinkedList<SExpCG>();
		
		for (PExp arg : args)
		{
			SExpCG argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg != null)
			{
				argsCg.add(argCg);
			}
			else
			{
				return null;
			}
		}
		
		boolean isStaticOrSl = Settings.dialect == Dialect.VDM_SL ||
				question.getTcFactory().createPDefinitionAssistant().isStatic(rootdef);

		while (rootdef instanceof AInheritedDefinition)
		{
			rootdef = ((AInheritedDefinition) rootdef).getSuperdef();
		}

		PType type = node.getType();
		ILexNameToken nameToken = node.getName();
		String name = nameToken.getName();

		AClassTypeCG classType = null;

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		boolean isConstructorCall = rootdef instanceof AExplicitOperationDefinition
				&& ((AExplicitOperationDefinition) rootdef).getIsConstructor();
		
		if(!isConstructorCall && !isStaticOrSl)
		{
			ILexNameToken rootDefClassName = node.getRootdef().getClassDefinition().getName();
			ILexNameToken enclosingClassName = node.getAncestor(SClassDefinition.class).getName();

			if (!rootDefClassName.equals(enclosingClassName))
			{

				ASuperCallStmCG superCall = new ASuperCallStmCG();
				superCall.setIsStatic(isStaticOrSl);
				superCall.setType(typeCg);
				superCall.setName(name);
				superCall.setArgs(argsCg);

				return superCall;
			}
		}
		else if (nameToken != null && nameToken.getExplicit() && isStaticOrSl)
		{
			String className = nameToken.getModule();
			classType = new AClassTypeCG();
			classType.setName(className);
		}

		APlainCallStmCG callStm = new APlainCallStmCG();
		
		callStm.setType(typeCg);
		callStm.setIsStatic(isStaticOrSl);
		callStm.setName(name);
		callStm.setClassType(classType);
		callStm.setArgs(argsCg);
		
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

		if (node.getExplicit())
		{
			SClassDefinition enclosingClass = node.getAncestor(SClassDefinition.class);
			
			if(enclosingClass != null)
			{
				if(!field.getModule().equals(enclosingClass.getName().getName()))
				{
					// A quoted method call is only supported if the explicit
					// module name is equal to that of the enclosing class. Say A
					// is a sub class of S and 'a' is an instance of A then a.A`op();
					//  is allowed (although it is the same as a.op()). However,
					// a.S`op(); is not allowed.
					question.addUnsupportedNode(node, "A quoted object call statement is only supported if the explicit module name is equal to that of the enclosing class");
				}
			}
			else
			{
				Logger.getLog().printErrorln("Could not find enclosing the statement of call a call object statement.");
			}
		}

		String fieldNameCg = field.getName();

		ACallObjectStmCG callObject = new ACallObjectStmCG();
		callObject.setType(typeCg);
		callObject.setDesignator(objectDesignatorCg);
		callObject.setFieldName(fieldNameCg);

		for (PExp arg : args)
		{
			SExpCG argCg = arg.apply(question.getExpVisitor(), question);

			if (argCg != null)
			{
				callObject.getArgs().add(argCg);
			}
			else
			{
				return null;
			}
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
		PExp set = node.getSet();
		PStm body = node.getStatement();

		SPatternCG patternCg = pattern.apply(question.getPatternVisitor(), question);
		SExpCG setExpCg = set.apply(question.getExpVisitor(), question);
		SStmCG bodyCg = body.apply(question.getStmVisitor(), question);

		AForAllStmCG forAll = new AForAllStmCG();
		forAll.setPattern(patternCg);
		forAll.setExp(setExpCg);
		forAll.setBody(bodyCg);

		return forAll;
	}

	@Override
	public SStmCG caseAForPatternBindStm(AForPatternBindStm node,
			IRInfo question) throws AnalysisException
	{
		//Example for mk_(a,b) in [mk_(1,2), mk_(3,4)] do skip;
		PPattern pattern = node.getPatternBind().getPattern();
		PExp exp = node.getExp();
		PStm stm = node.getStatement();
		Boolean reverse = node.getReverse();

		SPatternCG patternCg = pattern.apply(question.getPatternVisitor(), question);
		SExpCG seqExpCg = exp.apply(question.getExpVisitor(), question);
		SStmCG stmCg = stm.apply(question.getStmVisitor(), question);

		AForAllStmCG forAll = new AForAllStmCG();
		forAll.setPattern(patternCg);
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
		PType type = node.getType();
		PExp exp = node.getObj();

		if (exp.getType() instanceof ASetType)

		{
			STypeCG typeCG = type.apply(question.getTypeVisitor(), question);
			SExpCG expCG = exp.apply(question.getExpVisitor(), question);

			AStartlistStmCG s = new AStartlistStmCG();
			s.setType(typeCG);
			s.setExp(expCG);

			return s;
		} else
		{
			STypeCG typeCG = type.apply(question.getTypeVisitor(), question);
			SExpCG expCG = exp.apply(question.getExpVisitor(), question);

			AStartStmCG thread = new AStartStmCG();
			thread.setType(typeCG);
			thread.setExp(expCG);

			return thread;
		}
	}
}
