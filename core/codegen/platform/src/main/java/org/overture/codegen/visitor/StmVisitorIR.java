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
import org.overture.ast.statements.AExitStm;
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
import org.overture.ast.types.SSetType;
import org.overture.ast.types.PType;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.SObjectDesignatorIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStateDesignatorIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AReverseUnaryExpIR;
import org.overture.codegen.ir.expressions.AUndefinedExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignmentStmIR;
import org.overture.codegen.ir.statements.AAtomicStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.ir.statements.ACaseAltStmStmIR;
import org.overture.codegen.ir.statements.ACasesStmIR;
import org.overture.codegen.ir.statements.ACyclesStmIR;
import org.overture.codegen.ir.statements.ADurationStmIR;
import org.overture.codegen.ir.statements.AElseIfStmIR;
import org.overture.codegen.ir.statements.AErrorStmIR;
import org.overture.codegen.ir.statements.AExitStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AForIndexStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.ALetBeStStmIR;
import org.overture.codegen.ir.statements.ANotImplementedStmIR;
import org.overture.codegen.ir.statements.APeriodicStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.statements.ASkipStmIR;
import org.overture.codegen.ir.statements.AStartStmIR;
import org.overture.codegen.ir.statements.AStartlistStmIR;
import org.overture.codegen.ir.statements.ASuperCallStmIR;
import org.overture.codegen.ir.statements.AWhileStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.utils.AHeaderLetBeStIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.config.Settings;

public class StmVisitorIR extends AbstractVisitorIR<IRInfo, SStmIR>
{
	public StmVisitorIR()
	{
	}
	
	@Override
	public SStmIR caseAExitStm(AExitStm node, IRInfo question) throws AnalysisException
	{
		SExpIR expCg = node.getExpression() != null ? node.getExpression().apply(question.getExpVisitor(), question)
				: null;

		AExitStmIR exitCg = new AExitStmIR();
		exitCg.setExp(expCg);

		return exitCg;
	}

	@Override
	public SStmIR caseAErrorStm(AErrorStm node, IRInfo question)
			throws AnalysisException
	{
		return new AErrorStmIR();
	}
	
	@Override
	public SStmIR caseAClassInvariantStm(AClassInvariantStm node,
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
		
		AReturnStmIR returnStmCg = new AReturnStmIR();
		
		if(exps.isEmpty())
		{
			// Should not really be necessary
			returnStmCg.setExp(question.getExpAssistant().consBoolLiteral(true));
		}
		else if(exps.size() == 1)
		{
			SExpIR expCg = exps.get(0).apply(question.getExpVisitor(), question);
			returnStmCg.setExp(expCg);
		}
		else
		{
			// We have more than one expressions from which we will build an 'and chain'
			AAndBoolBinaryExpIR andExpTopCg = new AAndBoolBinaryExpIR();
			andExpTopCg.setType(new ABoolBasicTypeIR());
			andExpTopCg.setLeft(exps.get(0).apply(question.getExpVisitor(), question));
			
			AAndBoolBinaryExpIR previousAndExpCg = andExpTopCg;
			
			// The remaining ones except the last
			for(int i = 1; i < exps.size() - 1; i++)
			{
				SExpIR nextExpCg = exps.get(i).apply(question.getExpVisitor(), question);
				
				AAndBoolBinaryExpIR nextAndExpCg = new AAndBoolBinaryExpIR();
				nextAndExpCg.setType(new ABoolBasicTypeIR());
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
	public SStmIR caseAPeriodicStm(APeriodicStm node, IRInfo question)
			throws AnalysisException
	{
		String opName = node.getOpname().getName();
		
		APeriodicStmIR periodicStmCg = new APeriodicStmIR();
		periodicStmCg.setOpname(opName);
		
		for(PExp exp : node.getArgs())
		{
			SExpIR expCg = exp.apply(question.getExpVisitor(), question);
			
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
	public SStmIR caseAAtomicStm(AAtomicStm node, IRInfo question)
			throws AnalysisException
	{
		AAtomicStmIR atomicBlock = new AAtomicStmIR();
		
		for (AAssignmentStm assignment : node.getAssignments())
		{
			SStmIR stmCg = assignment.apply(question.getStmVisitor(), question);
			
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
	public SStmIR caseACyclesStm(ACyclesStm node, IRInfo question) throws AnalysisException
	{
		PExp cycles = node.getCycles();
		PStm stm = node.getStatement();
		
		SExpIR cyclesCg = cycles.apply(question.getExpVisitor(), question);
		SStmIR stmCg = stm.apply(question.getStmVisitor(), question);
		
		ACyclesStmIR cycStm = new ACyclesStmIR();
		cycStm.setCycles(cyclesCg);
		cycStm.setStm(stmCg);
		
		return cycStm;
	}
	
	@Override
	public SStmIR caseADurationStm(ADurationStm node, IRInfo question) throws AnalysisException
	{
		PExp duration = node.getDuration();
		PStm stm = node.getStatement();
		
		SExpIR durationCg = duration.apply(question.getExpVisitor(), question);
		SStmIR stmCg = stm.apply(question.getStmVisitor(), question);
		
		ADurationStmIR durStm = new ADurationStmIR();
		durStm.setDuration(durationCg);
		durStm.setStm(stmCg);;
		
		return durStm;
	}

	@Override
	public SStmIR caseALetBeStStm(ALetBeStStm node, IRInfo question)
			throws AnalysisException
	{
		PMultipleBind multipleBind = node.getBind();
		SMultipleBindIR multipleBindCg = multipleBind.apply(question.getMultipleBindVisitor(), question);
		
		PExp suchThat = node.getSuchThat();
		PStm stm = node.getStatement();

		SExpIR suchThatCg = suchThat != null ? suchThat.apply(question.getExpVisitor(), question)
				: null;
		SStmIR stmCg = stm.apply(question.getStmVisitor(), question);

		ALetBeStStmIR letBeSt = new ALetBeStStmIR();

		AHeaderLetBeStIR header = question.getExpAssistant().consHeader(multipleBindCg, suchThatCg);

		letBeSt.setHeader(header);
		letBeSt.setStatement(stmCg);

		return letBeSt;
	}

	@Override
	public SStmIR caseAWhileStm(AWhileStm node, IRInfo question)
			throws AnalysisException
	{
		PStm stm = node.getStatement();
		PExp exp = node.getExp();

		SStmIR bodyCg = stm.apply(question.getStmVisitor(), question);
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		AWhileStmIR whileStm = new AWhileStmIR();
		whileStm.setExp(expCg);
		whileStm.setBody(bodyCg);

		return whileStm;
	}

	@Override
	public SStmIR caseANotYetSpecifiedStm(ANotYetSpecifiedStm node,
			IRInfo question) throws AnalysisException
	{
		return new ANotImplementedStmIR();
	}

	@Override
	public SStmIR caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
			IRInfo question) throws AnalysisException
	{
		ABlockStmIR blockStm = new ABlockStmIR();
		blockStm.setScoped(question.getStmAssistant().isScoped(node));

		LinkedList<AAssignmentDefinition> assignmentDefs = node.getAssignmentDefs();

		for (AAssignmentDefinition def : assignmentDefs)
		{
			PType type = def.getType();
			String name = def.getName().getName();
			PExp exp = def.getExpression();

			STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
			AIdentifierPatternIR idPattern = new AIdentifierPatternIR();
			idPattern.setName(name);
			SExpIR expCg = exp.apply(question.getExpVisitor(), question);
			
			AVarDeclIR localDecl = question.getDeclAssistant().consLocalVarDecl(def, typeCg, idPattern, expCg);
			
			if (expCg instanceof AUndefinedExpIR)
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
			SStmIR stmCg = pStm.apply(question.getStmVisitor(), question);

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
	public SStmIR caseAAssignmentStm(AAssignmentStm node, IRInfo question)
			throws AnalysisException
	{
		PStateDesignator target = node.getTarget();
		PExp exp = node.getExp();

		SStateDesignatorIR targetCg = target.apply(question.getStateDesignatorVisitor(), question);
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		AAssignmentStmIR assignment = new AAssignmentStmIR();
		assignment.setTarget(targetCg);
		assignment.setExp(expCg);

		return assignment;
	}

	@Override
	public SStmIR caseALetStm(ALetStm node, IRInfo question)
			throws AnalysisException
	{
		ABlockStmIR block = new ABlockStmIR();
		block.setScoped(question.getStmAssistant().isScoped(node));
		
		question.getDeclAssistant().setFinalLocalDefs(node.getLocalDefs(), block.getLocalDefs(), question);

		SStmIR stm = node.getStatement().apply(question.getStmVisitor(), question);
		
		if (stm != null)
		{
			block.getStatements().add(stm);
		}

		return block;
	}

	@Override
	public SStmIR caseAReturnStm(AReturnStm node, IRInfo question)
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
				return new AReturnStmIR();
			} else
			{
				question.addUnsupportedNode(operation, "Unexpected expression returned by constructor: Values expliclty returned by constructors must be 'self'.");
				return null;
			}
		}

		AReturnStmIR returnStm = new AReturnStmIR();

		if (exp != null)
		{
			SExpIR expCg = exp.apply(question.getExpVisitor(), question);
			returnStm.setExp(expCg);
		}

		return returnStm;
	}

	@Override
	public SStmIR caseACallStm(ACallStm node, IRInfo question)
			throws AnalysisException
	{
		PDefinition rootdef = node.getRootdef();
		LinkedList<PExp> args = node.getArgs();

		List<SExpIR> argsCg = new LinkedList<SExpIR>();
		
		for (PExp arg : args)
		{
			SExpIR argCg = arg.apply(question.getExpVisitor(), question);

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

		AClassTypeIR classType = null;

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		
		boolean isConstructorCall = rootdef instanceof AExplicitOperationDefinition
				&& ((AExplicitOperationDefinition) rootdef).getIsConstructor();
		
		if(!isConstructorCall && !isStaticOrSl)
		{
			ILexNameToken enclosingClassName = node.getAncestor(SClassDefinition.class).getName();

			if (node.getName().getExplicit() && !node.getName().equals(enclosingClassName))
			{
				ASuperCallStmIR superCall = new ASuperCallStmIR();
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
			classType = new AClassTypeIR();
			classType.setName(className);
		}

		APlainCallStmIR callStm = new APlainCallStmIR();
		
		callStm.setType(typeCg);
		callStm.setIsStatic(isStaticOrSl);
		callStm.setName(name);
		callStm.setClassType(classType);
		callStm.setArgs(argsCg);
		
		return callStm;
	}

	@Override
	public SStmIR caseACallObjectStm(ACallObjectStm node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PObjectDesignator objectDesignator = node.getDesignator();
		ILexNameToken field = node.getField();
		LinkedList<PExp> args = node.getArgs();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SObjectDesignatorIR objectDesignatorCg = objectDesignator.apply(question.getObjectDesignatorVisitor(), question);

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

		ACallObjectStmIR callObject = new ACallObjectStmIR();
		callObject.setType(typeCg);
		callObject.setDesignator(objectDesignatorCg);
		callObject.setFieldName(fieldNameCg);

		for (PExp arg : args)
		{
			SExpIR argCg = arg.apply(question.getExpVisitor(), question);

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
	public SStmIR caseAElseIfStm(AElseIfStm node, IRInfo question)
			throws AnalysisException
	{
		// Don't visit it but create it directly if needed in the ifStm in order to avoid casting
		return null;
	}

	@Override
	public SStmIR caseACasesStm(ACasesStm node, IRInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		PStm others = node.getOthers();
		LinkedList<ACaseAlternativeStm> cases = node.getCases();

		SExpIR expCg = exp.apply(question.getExpVisitor(), question);
		SStmIR othersCg = others != null ? others.apply(question.getStmVisitor(), question)
				: null;

		ACasesStmIR casesStmCg = new ACasesStmIR();
		casesStmCg.setExp(expCg);
		casesStmCg.setOthers(othersCg);

		question.getStmAssistant().handleAlternativesCasesStm(question, exp, cases, casesStmCg.getCases());

		return casesStmCg;
	}

	@Override
	public SStmIR caseACaseAlternativeStm(ACaseAlternativeStm node,
			IRInfo question) throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		PStm result = node.getResult();

		SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);
		SStmIR resultCg = result.apply(question.getStmVisitor(), question);

		ACaseAltStmStmIR caseCg = new ACaseAltStmStmIR();
		caseCg.setPattern(patternCg);
		caseCg.setResult(resultCg);

		return caseCg;
	}

	@Override
	public SStmIR caseAIfStm(AIfStm node, IRInfo question)
			throws AnalysisException
	{
		SExpIR ifExp = node.getIfExp().apply(question.getExpVisitor(), question);
		SStmIR thenStm = node.getThenStm().apply(question.getStmVisitor(), question);

		AIfStmIR ifStm = new AIfStmIR();

		ifStm.setIfExp(ifExp);
		ifStm.setThenStm(thenStm);

		LinkedList<AElseIfStm> elseIfs = node.getElseIf();

		for (AElseIfStm stm : elseIfs)
		{
			ifExp = stm.getElseIf().apply(question.getExpVisitor(), question);
			thenStm = stm.getThenStm().apply(question.getStmVisitor(), question);

			AElseIfStmIR elseIfStm = new AElseIfStmIR();
			elseIfStm.setElseIf(ifExp);
			elseIfStm.setThenStm(thenStm);

			ifStm.getElseIf().add(elseIfStm);
		}

		if (node.getElseStm() != null)
		{
			SStmIR elseStm = node.getElseStm().apply(question.getStmVisitor(), question);
			ifStm.setElseStm(elseStm);
		}

		return ifStm;

	}

	@Override
	public SStmIR caseASkipStm(ASkipStm node, IRInfo question)
			throws AnalysisException
	{
		return new ASkipStmIR();
	}

	@Override
	public SStmIR caseASubclassResponsibilityStm(
			ASubclassResponsibilityStm node, IRInfo question)
			throws AnalysisException
	{
		return null;// Indicates an abstract body
	}

	@Override
	public SStmIR caseAForIndexStm(AForIndexStm node, IRInfo question)
			throws AnalysisException
	{
		ILexNameToken var = node.getVar();
		PExp from = node.getFrom();
		PExp to = node.getTo();
		PExp by = node.getBy();
		PStm stm = node.getStatement();

		String varCg = var.getName();
		SExpIR fromCg = from.apply(question.getExpVisitor(), question);
		SExpIR toCg = to.apply(question.getExpVisitor(), question);
		SExpIR byCg = by != null ? by.apply(question.getExpVisitor(), question)
				: null;
		SStmIR bodyCg = stm.apply(question.getStmVisitor(), question);

		AForIndexStmIR forStm = new AForIndexStmIR();
		forStm.setVar(varCg);
		forStm.setFrom(fromCg);
		forStm.setTo(toCg);
		forStm.setBy(byCg);
		forStm.setBody(bodyCg);

		return forStm;
	}

	@Override
	public SStmIR caseAForAllStm(AForAllStm node, IRInfo question)
			throws AnalysisException
	{
		//Example: for all x in set {1,2,3} do skip;
		PPattern pattern = node.getPattern();
		PExp set = node.getSet();
		PStm body = node.getStatement();

		SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);
		SExpIR setExpCg = set.apply(question.getExpVisitor(), question);
		SStmIR bodyCg = body.apply(question.getStmVisitor(), question);

		AForAllStmIR forAll = new AForAllStmIR();
		forAll.setPattern(patternCg);
		forAll.setExp(setExpCg);
		forAll.setBody(bodyCg);

		return forAll;
	}

	@Override
	public SStmIR caseAForPatternBindStm(AForPatternBindStm node,
			IRInfo question) throws AnalysisException
	{
		//Example for mk_(a,b) in [mk_(1,2), mk_(3,4)] do skip;
		PPattern pattern = node.getPatternBind().getPattern();
		PExp exp = node.getExp();
		PStm stm = node.getStatement();
		Boolean reverse = node.getReverse();

		SPatternIR patternCg = pattern.apply(question.getPatternVisitor(), question);
		SExpIR seqExpCg = exp.apply(question.getExpVisitor(), question);
		SStmIR stmCg = stm.apply(question.getStmVisitor(), question);

		AForAllStmIR forAll = new AForAllStmIR();
		forAll.setPattern(patternCg);
		forAll.setBody(stmCg);

		if (reverse != null && reverse)
		{
			AReverseUnaryExpIR reversedExp = new AReverseUnaryExpIR();
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
	public SStmIR caseAStartStm(AStartStm node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PExp exp = node.getObj();

		if (exp.getType() instanceof SSetType)

		{
			STypeIR typeIR = type.apply(question.getTypeVisitor(), question);
			SExpIR expIR = exp.apply(question.getExpVisitor(), question);

			AStartlistStmIR s = new AStartlistStmIR();
			s.setType(typeIR);
			s.setExp(expIR);

			return s;
		} else
		{
			STypeIR typeIR = type.apply(question.getTypeVisitor(), question);
			SExpIR expIR = exp.apply(question.getExpVisitor(), question);

			AStartStmIR thread = new AStartStmIR();
			thread.setType(typeIR);
			thread.setExp(expIR);

			return thread;
		}
	}
}
