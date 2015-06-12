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
package org.overture.codegen.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACaseAltExpExpCG;
import org.overture.codegen.cgast.expressions.ACasesExpCG;
import org.overture.codegen.cgast.expressions.ACompMapExpCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.ACompSetExpCG;
import org.overture.codegen.cgast.expressions.AEnumMapExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExists1QuantifierExpCG;
import org.overture.codegen.cgast.expressions.AExistsQuantifierExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AForAllQuantifierExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.AMapletExpCG;
import org.overture.codegen.cgast.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARecordModExpCG;
import org.overture.codegen.cgast.expressions.ARecordModifierCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.expressions.SBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.SQuantifierExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ABreakStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
import org.overture.codegen.cgast.statements.ACasesStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.AWhileStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.comp.ComplexCompStrategy;
import org.overture.codegen.trans.comp.MapCompStrategy;
import org.overture.codegen.trans.comp.SeqCompStrategy;
import org.overture.codegen.trans.comp.SetCompStrategy;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.let.LetBeStStrategy;
import org.overture.codegen.trans.quantifier.Exists1CounterData;
import org.overture.codegen.trans.quantifier.Exists1QuantifierStrategy;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifier;
import org.overture.codegen.trans.quantifier.OrdinaryQuantifierStrategy;

public class TransformationVisitor extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;

	//TODO: consider putting in ir info
	private List<AClassDeclCG> classes;
	
	private TransAssistantCG transformationAssistant;

	private Exists1CounterData counterData;
	
	private ILanguageIterator langIterator;

	private String ternaryIfExpPrefix;
	private String casesExpResultPrefix;
	private String andExpPrefix;
	private String orExpPrefix;
	private String whileCondExpPrefix;
	private String recModifierExpPrefix;

	public TransformationVisitor(IRInfo info, List<AClassDeclCG> classes, TempVarPrefixes varPrefixes,
			TransAssistantCG transformationAssistant, Exists1CounterData counterData,
			ILanguageIterator langIterator, String ternaryIfExpPrefix, String casesExpPrefix, String andExpPrefix, String orExpPrefix, String whileCondExpPrefix, String recModifierExpPrefix)
	{
		this.info = info;
		this.classes = classes;
		this.transformationAssistant = transformationAssistant;
		this.counterData = counterData;
		this.langIterator = langIterator;
		
		this.ternaryIfExpPrefix = ternaryIfExpPrefix;
		this.casesExpResultPrefix = casesExpPrefix;
		this.andExpPrefix = andExpPrefix;
		this.orExpPrefix = orExpPrefix;
		this.whileCondExpPrefix = whileCondExpPrefix;
		this.recModifierExpPrefix = recModifierExpPrefix;
	}
	
	@Override
	public void caseATernaryIfExpCG(ATernaryIfExpCG node)
			throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.findEnclosingStm(node);
		
		if(enclosingStm == null)
		{
			// TODO:
			// Cases such as
			// values
			// public x = 1 + if 2 = 3 then 4 else 5 + 6;
			// Will not be treated
			return;
		}
		
		String resultVarName = info.getTempVarNameGen().nextVarName(ternaryIfExpPrefix);
		
		AVarDeclCG resultDecl = transformationAssistant.consDecl(resultVarName, node.getType().clone(), info.getExpAssistant().consNullExp());
		AIdentifierVarExpCG resultVar = transformationAssistant.consIdentifierVar(resultVarName, resultDecl.getType().clone());
		
		SExpCG condition = node.getCondition();
		SExpCG trueValue = node.getTrueValue();
		SExpCG falseValue = node.getFalseValue();
		
		AAssignToExpStmCG trueBranch = new AAssignToExpStmCG();
		trueBranch.setTarget(resultVar.clone());
		trueBranch.setExp(trueValue.clone());
		
		AAssignToExpStmCG falseBranch = new AAssignToExpStmCG();
		falseBranch.setTarget(resultVar.clone());
		falseBranch.setExp(falseValue);

		AIfStmCG ifStm = new AIfStmCG();
		ifStm.setIfExp(condition.clone());
		ifStm.setThenStm(trueBranch);
		ifStm.setElseStm(falseBranch);
		
		ABlockStmCG replacementBlock = new ABlockStmCG();

		transformationAssistant.replaceNodeWith(node, resultVar);
		transformationAssistant.replaceNodeWith(enclosingStm, replacementBlock);
		
		ABlockStmCG declBlock = new ABlockStmCG();
		declBlock.getLocalDefs().add(resultDecl);
		
		replacementBlock.getStatements().add(declBlock);
		replacementBlock.getStatements().add(ifStm);
		replacementBlock.getStatements().add(enclosingStm);
		
		ifStm.getIfExp().apply(this);
		trueBranch.getExp().apply(this);
		falseBranch.getExp().apply(this);
	}
	
	@Override
	public void caseAWhileStmCG(AWhileStmCG node) throws AnalysisException
	{
		// while(boolExp) { body; }
		//
		// boolExp is replaced with a variable expression 'whileCond' that is
		// computed as set for each iteration in the while loop:
		//
		// boolean whileCond = true;
		//
		// while(whileCond)
		// {
		//   whileCond = boolExp;
		//   if (!whileCond) { break; }
		//   body;
		// }
		//
		// This is needed for cases where the while condition is a complex
		// expression that needs to be transformed. For example, when the
		// while condition is a quantified expression
		
		SExpCG exp = node.getExp().clone();
		SStmCG body = node.getBody().clone();
		
		String whileCondName = info.getTempVarNameGen().nextVarName(whileCondExpPrefix);
		
		SExpCG whileCondVar = transformationAssistant.consBoolCheck(whileCondName, false);
		
		AIfStmCG whileCondCheck = new AIfStmCG();
		whileCondCheck.setIfExp(transformationAssistant.consBoolCheck(whileCondName, true));
		whileCondCheck.setThenStm(new ABreakStmCG());
		
		ABlockStmCG newWhileBody = new ABlockStmCG();
		newWhileBody.getStatements().add(transformationAssistant.consBoolVarAssignment(exp, whileCondName));
		newWhileBody.getStatements().add(whileCondCheck);
		newWhileBody.getStatements().add(body);
		
		AWhileStmCG newWhileStm = new AWhileStmCG();
		newWhileStm.setExp(whileCondVar);
		newWhileStm.setBody(newWhileBody);
		
		ABlockStmCG declBlock = new ABlockStmCG();
		AVarDeclCG whileCondVarDecl = transformationAssistant.consBoolVarDecl(whileCondName, true);
		declBlock.getLocalDefs().add(whileCondVarDecl);
		declBlock.getStatements().add(newWhileStm);
		
		transformationAssistant.replaceNodeWith(node, declBlock);

		newWhileStm.getBody().apply(this);
	}
	
	@Override
	public void caseAOrBoolBinaryExpCG(AOrBoolBinaryExpCG node)
			throws AnalysisException
	{
		// left || right 
		//
		// is replaced with a variable expression 'orResult' that is
		// computed as:
		//
		// boolean orResult = false;
		// if (left) 
		// {
		//	  orResult = true;
		// }
		// else
		// {
		//    orResult = right;
		// }
		//
		
		SStmCG enclosingStm = transformationAssistant.findEnclosingStm(node);
		
		if(transformBoolBinaryExp(node, enclosingStm))
		{
			String resultName = info.getTempVarNameGen().nextVarName(orExpPrefix);
			handleLogicExp(node, enclosingStm, consOrExpCheck(node, resultName), resultName);
		}
		else
		{
			visitBoolBinary(node);
		}
	}

	@Override
	public void caseAAndBoolBinaryExpCG(AAndBoolBinaryExpCG node)
			throws AnalysisException
	{
		// left && right 
		//
		// is replaced with a variable expression 'andResult' that is
		// computed as:
		//
		// boolean andResult = false;
		// if (left) 
		// { 
		//    if (right)
		//    {
		//       andResult = true;
		//    }
		// }
		
		SStmCG enclosingStm = transformationAssistant.findEnclosingStm(node);

		if(transformBoolBinaryExp(node, enclosingStm))
		{
			String resultName = info.getTempVarNameGen().nextVarName(andExpPrefix);
			handleLogicExp(node, enclosingStm, consAndExpCheck(node, resultName), resultName);
		}
		else
		{
			visitBoolBinary(node);
		}
	}

	@Override
	public void caseALetBeStStmCG(ALetBeStStmCG node) throws AnalysisException
	{
		AHeaderLetBeStCG header = node.getHeader();
		SExpCG suchThat = header.getSuchThat();
		SSetTypeCG setType = transformationAssistant.getSetTypeCloned(header.getBinding().getSet());
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		LetBeStStrategy strategy = new LetBeStStrategy(transformationAssistant, suchThat, setType, langIterator, tempVarNameGen, varPrefixes);

		ASetMultipleBindCG binding = header.getBinding();

		if (transformationAssistant.hasEmptySet(binding))
		{
			transformationAssistant.cleanUpBinding(binding);
			node.setStatement(new ABlockStmCG());
		}

		LinkedList<SPatternCG> patterns = binding.getPatterns();
		ABlockStmCG outerBlock = transformationAssistant.consIterationBlock(patterns, binding.getSet(), tempVarNameGen, strategy);

		// Only the statement of the let be st statement is added to the outer block statements.
		// We obtain the equivalent functionality of the remaining part of the let be st statement
		// from the transformation in the outer block
		outerBlock.getStatements().add(node.getStatement());

		// Replace the let be st statement with the transformation
		transformationAssistant.replaceNodeWithRecursively(node, outerBlock, this);
		
		outerBlock.setScoped(info.getStmAssistant().isScoped(outerBlock));
	}

	@Override
	public void caseALetBeStExpCG(ALetBeStExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "let be st expressions");

		AHeaderLetBeStCG header = node.getHeader();
		ASetMultipleBindCG binding = header.getBinding();
		SExpCG suchThat = header.getSuchThat();
		SSetTypeCG setType = transformationAssistant.getSetTypeCloned(binding.getSet());
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		LetBeStStrategy strategy = new LetBeStStrategy(transformationAssistant, suchThat, setType, langIterator, tempVarNameGen, varPrefixes);

		ABlockStmCG outerBlock = new ABlockStmCG();

		SExpCG letBeStResult = null;

		if (transformationAssistant.hasEmptySet(binding))
		{
			transformationAssistant.cleanUpBinding(binding);
			letBeStResult = info.getExpAssistant().consNullExp();
		} else
		{
			String var = tempVarNameGen.nextVarName(IRConstants.GENERATED_TEMP_LET_BE_ST_EXP_NAME_PREFIX);
			SExpCG value = node.getValue();

			AVarDeclCG resultDecl = transformationAssistant.consDecl(var, value.getType().clone(), info.getExpAssistant().consNullExp());
			outerBlock.getLocalDefs().add(resultDecl);
			
			AAssignToExpStmCG setLetBeStResult = new AAssignToExpStmCG();
			setLetBeStResult.setTarget(transformationAssistant.consIdentifierVar(var, value.getType().clone()));
			setLetBeStResult.setExp(value);
			outerBlock.getStatements().add(setLetBeStResult);

			AIdentifierVarExpCG varExpResult = new AIdentifierVarExpCG();
			varExpResult.setType(value.getType().clone());
			varExpResult.setIsLocal(true);
			varExpResult.setName(var);
			letBeStResult = varExpResult;
		}

		// Replace the let be st expression with the result expression
		transformationAssistant.replaceNodeWith(node, letBeStResult);

		LinkedList<SPatternCG> patterns = binding.getPatterns();
		ABlockStmCG block = transformationAssistant.consIterationBlock(patterns, binding.getSet(), tempVarNameGen, strategy);
		outerBlock.getStatements().addFirst(block);

		// Replace the enclosing statement with the transformation
		transformationAssistant.replaceNodeWith(enclosingStm, outerBlock);

		// And make sure to have the enclosing statement in the transformed tree
		outerBlock.getStatements().add(enclosingStm);
		outerBlock.apply(this);
		
		outerBlock.setScoped(info.getStmAssistant().isScoped(outerBlock));
	}
	
	@Override
	public void caseARecordModExpCG(ARecordModExpCG node)
			throws AnalysisException
	{
		String recModifierName = info.getTempVarNameGen().nextVarName(recModifierExpPrefix);
		
		AVarDeclCG recDecl = transformationAssistant.consDecl(recModifierName, node.getType().clone(), node.getRec().clone());
		ABlockStmCG declStm = new ABlockStmCG();
		declStm.getLocalDefs().add(recDecl);
		
		AIdentifierVarExpCG recVar = transformationAssistant.consIdentifierVar(recModifierName, node.getType().clone());
		
		ABlockStmCG replacementBlock = new ABlockStmCG();
		replacementBlock.getStatements().add(declStm);
		
		for(ARecordModifierCG modifier : node.getModifiers())
		{
			String name = modifier.getName();
			SExpCG value = modifier.getValue().clone();
			
			STypeCG fieldType = info.getTypeAssistant().getFieldType(classes, node.getRecType(), name);
			
			AFieldExpCG field = new AFieldExpCG();
			field.setType(fieldType);
			field.setObject(recVar.clone());
			field.setMemberName(name);
			
			AAssignToExpStmCG assignment = new AAssignToExpStmCG();
			assignment.setTarget(field);
			assignment.setExp(value);
			
			replacementBlock.getStatements().add(assignment);
		}
		
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "record modification expression");
		
		transform(enclosingStm, replacementBlock, recVar.clone(), node);
		
		replacementBlock.apply(this);
	}

	@Override
	public void caseACompMapExpCG(ACompMapExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "map comprehension");

		AMapletExpCG first = node.getFirst();
		SExpCG predicate = node.getPredicate();
		STypeCG type = node.getType();
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(IRConstants.GENERATED_TEMP_MAP_COMP_NAME_PREFIX);
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		ComplexCompStrategy strategy = new MapCompStrategy(transformationAssistant, first, predicate, var, type, langIterator, tempVarNameGen, varPrefixes);

		LinkedList<ASetMultipleBindCG> bindings = node.getBindings();
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(bindings, tempVarNameGen, strategy);

		if (block.getStatements().isEmpty())
		{
			// In case the block has no statements the result of the map comprehension is the empty map
			AEnumMapExpCG emptyMap = new AEnumMapExpCG();
			emptyMap.setType(type.clone());

			// Replace the map comprehension with the empty map
			transformationAssistant.replaceNodeWith(node, emptyMap);
		} else
		{
			replaceCompWithTransformation(enclosingStm, block, type, var, node);
		}

		block.apply(this);
	}

	@Override
	public void caseACompSetExpCG(ACompSetExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "set comprehension");

		SExpCG first = node.getFirst();
		SExpCG predicate = node.getPredicate();
		STypeCG type = node.getType();
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(IRConstants.GENERATED_TEMP_SET_COMP_NAME_PREFIX);
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		ComplexCompStrategy strategy = new SetCompStrategy(transformationAssistant, first, predicate, var, type, langIterator, tempVarNameGen, varPrefixes);

		LinkedList<ASetMultipleBindCG> bindings = node.getBindings();
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(bindings, tempVarNameGen, strategy);

		if (block.getStatements().isEmpty())
		{
			// In case the block has no statements the result of the set comprehension is the empty set
			AEnumSetExpCG emptySet = new AEnumSetExpCG();
			emptySet.setType(type.clone());

			// Replace the set comprehension with the empty set
			transformationAssistant.replaceNodeWith(node, emptySet);
		} else
		{
			replaceCompWithTransformation(enclosingStm, block, type, var, node);
		}

		block.apply(this);
	}

	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "sequence comprehension");

		SExpCG first = node.getFirst();
		SExpCG predicate = node.getPredicate();
		STypeCG type = node.getType();
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(IRConstants.GENERATED_TEMP_SEQ_COMP_NAME_PREFIX);
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		SeqCompStrategy strategy = new SeqCompStrategy(transformationAssistant, first, predicate, var, type, langIterator, tempVarNameGen, varPrefixes);

		if (transformationAssistant.isEmptySet(node.getSet()))
		{
			// In case the block has no statements the result of the sequence comprehension is the empty sequence
			AEnumSeqExpCG emptySeq = new AEnumSeqExpCG();
			emptySeq.setType(type.clone());

			// Replace the sequence comprehension with the empty sequence
			transformationAssistant.replaceNodeWith(node, emptySeq);
		} else
		{
			LinkedList<SPatternCG> patterns = new LinkedList<SPatternCG>();
			patterns.add(node.getSetBind().getPattern().clone());

			ABlockStmCG block = transformationAssistant.consIterationBlock(patterns, node.getSet(), info.getTempVarNameGen(), strategy);

			replaceCompWithTransformation(enclosingStm, block, type, var, node);

			block.apply(this);
		}
	}

	@Override
	public void caseAForAllQuantifierExpCG(AForAllQuantifierExpCG node)
			throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "forall expression");

		SExpCG predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(IRConstants.GENERATED_TEMP_FORALL_EXP_NAME_PREFIX);
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		OrdinaryQuantifierStrategy strategy = new OrdinaryQuantifierStrategy(transformationAssistant, predicate, var, OrdinaryQuantifier.FORALL, langIterator, tempVarNameGen, varPrefixes);

		List<ASetMultipleBindCG> multipleSetBinds = filterMultipleBinds(node);
		
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpCG forAllResult = info.getExpAssistant().consBoolLiteral(true);
			transformationAssistant.replaceNodeWith(node, forAllResult);
		} else
		{
			AIdentifierVarExpCG forAllResult = new AIdentifierVarExpCG();
			forAllResult.setIsLocal(true);
			forAllResult.setType(new ABoolBasicTypeCG());
			forAllResult.setName(var);

			transform(enclosingStm, block, forAllResult, node);
			block.apply(this);
		}
	}

	@Override
	public void caseAExistsQuantifierExpCG(AExistsQuantifierExpCG node)
			throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "exists expression");

		SExpCG predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(IRConstants.GENERATED_TEMP_EXISTS_EXP_NAME_PREFIX);
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		OrdinaryQuantifierStrategy strategy = new OrdinaryQuantifierStrategy(transformationAssistant, predicate, var, OrdinaryQuantifier.EXISTS, langIterator, tempVarNameGen, varPrefixes);

		List<ASetMultipleBindCG> multipleSetBinds = filterMultipleBinds(node);
		
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpCG existsResult = info.getExpAssistant().consBoolLiteral(false);
			transformationAssistant.replaceNodeWith(node, existsResult);
		} else
		{
			AIdentifierVarExpCG existsResult = new AIdentifierVarExpCG();
			existsResult.setIsLocal(true);
			existsResult.setType(new ABoolBasicTypeCG());
			existsResult.setName(var);

			transform(enclosingStm, block, existsResult, node);
			block.apply(this);
		}
	}

	@Override
	public void caseAExists1QuantifierExpCG(AExists1QuantifierExpCG node)
			throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "exists1 expression");

		SExpCG predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = info.getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(IRConstants.GENERATED_TEMP_EXISTS1_EXP_NAME_PREFIX);
		TempVarPrefixes varPrefixes = transformationAssistant.getVarPrefixes();

		Exists1QuantifierStrategy strategy = new Exists1QuantifierStrategy(transformationAssistant, predicate, var, langIterator, tempVarNameGen, varPrefixes, counterData);
		
		List<ASetMultipleBindCG> multipleSetBinds = filterMultipleBinds(node);
		
		ABlockStmCG block = transformationAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpCG exists1Result = info.getExpAssistant().consBoolLiteral(false);
			transformationAssistant.replaceNodeWith(node, exists1Result);
		} else
		{
			AIdentifierVarExpCG counter = new AIdentifierVarExpCG();
			counter.setType(new AIntNumericBasicTypeCG());
			counter.setIsLocal(true);
			counter.setName(var);

			AEqualsBinaryExpCG exists1Result = new AEqualsBinaryExpCG();
			exists1Result.setType(new ABoolBasicTypeCG());
			exists1Result.setLeft(counter);
			exists1Result.setRight(info.getExpAssistant().consIntLiteral(1));

			transform(enclosingStm, block, exists1Result, node);
			block.apply(this);
		}
	}

	public void caseALetDefExpCG(ALetDefExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "let def expression");

		SExpCG exp = node.getExp();
		transformationAssistant.replaceNodeWith(node, exp);

		ABlockStmCG topBlock = new ABlockStmCG();
		ABlockStmCG current = topBlock;

		for (AVarDeclCG local : node.getLocalDefs())
		{
			ABlockStmCG tmp = new ABlockStmCG();
			tmp.getLocalDefs().add(local.clone());
			current.getStatements().add(tmp);
			current = tmp;
		}

		transformationAssistant.replaceNodeWith(enclosingStm, topBlock);
		topBlock.getStatements().add(enclosingStm);

		exp.apply(this);
		topBlock.apply(this);
		
		topBlock.setScoped(info.getStmAssistant().isScoped(topBlock));
	}

	private void replaceCompWithTransformation(SStmCG enclosingStm,
			ABlockStmCG block, STypeCG type, String var, SExpCG comp)
	{
		AIdentifierVarExpCG compResult = new AIdentifierVarExpCG();
		compResult.setType(type.clone());
		compResult.setName(var);
		compResult.setIsLambda(false);
		compResult.setIsLocal(true);

		transform(enclosingStm, block, compResult, comp);
	}

	private void transform(SStmCG enclosingStm, ABlockStmCG block,
			SExpCG nodeResult, SExpCG node)
	{
		// Replace the node with the node result
		transformationAssistant.replaceNodeWith(node, nodeResult);

		// Replace the enclosing statement with the transformation
		transformationAssistant.replaceNodeWith(enclosingStm, block);

		// And make sure to have the enclosing statement in the transformed tree
		block.getStatements().add(enclosingStm);
	}

	private AAssignToExpStmCG assignToVar(AIdentifierVarExpCG var,
			SExpCG exp)
	{
		AAssignToExpStmCG assignment = new AAssignToExpStmCG();
		assignment.setTarget(var.clone());
		assignment.setExp(exp.clone());

		return assignment;
	}

	@Override
	public void caseACasesExpCG(ACasesExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transformationAssistant.getEnclosingStm(node, "cases expression");

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		String casesExpResultName = info.getTempVarNameGen().nextVarName(casesExpResultPrefix);
		idPattern.setName(casesExpResultName);

		AVarDeclCG resultVarDecl = info.getDeclAssistant().consLocalVarDecl(node.getType().clone(),
				idPattern, new AUndefinedExpCG());

		AIdentifierVarExpCG resultVar = new AIdentifierVarExpCG();
		resultVar.setIsLocal(true);
		resultVar.setIsLambda(false);
		resultVar.setName(casesExpResultName);
		resultVar.setType(node.getType().clone());

		ACasesStmCG casesStm = new ACasesStmCG();
		casesStm.setExp(node.getExp().clone());

		for (ACaseAltExpExpCG altExp : node.getCases())
		{
			ACaseAltStmStmCG altStm = new ACaseAltStmStmCG();
			altStm.setPattern(altExp.getPattern().clone());
			altStm.setResult(assignToVar(resultVar, altExp.getResult()));
			altStm.setPatternType(altExp.getPatternType().clone());

			casesStm.getCases().add(altStm);
		}

		if (node.getOthers() != null)
		{
			casesStm.setOthers(assignToVar(resultVar, node.getOthers()));
		}

		ABlockStmCG block = new ABlockStmCG();

		ABlockStmCG wrapperBlock = new ABlockStmCG();
		wrapperBlock.getLocalDefs().add(resultVarDecl);

		block.getStatements().add(wrapperBlock);
		block.getStatements().add(casesStm);

		transform(enclosingStm, block, resultVar, node);

		casesStm.apply(this);
	}

	private AIfStmCG consAndExpCheck(AAndBoolBinaryExpCG node, String andResultVarName)
	{
		SExpCG left = node.getLeft().clone();
		SExpCG right = node.getRight().clone();
		
		AIfStmCG leftCheck = new AIfStmCG();
		leftCheck.setIfExp(left);
		
		AIfStmCG rightCheck = new AIfStmCG();
		rightCheck.setIfExp(right);
		
		AAssignToExpStmCG assignAndVar = new AAssignToExpStmCG();
		assignAndVar.setTarget(transformationAssistant.consBoolCheck(andResultVarName, false));
		assignAndVar.setExp(info.getAssistantManager().getExpAssistant().consBoolLiteral(true));
		
		rightCheck.setThenStm(assignAndVar);
		
		leftCheck.setThenStm(rightCheck);
		
		return leftCheck;
	}
	
	private SStmCG consOrExpCheck(AOrBoolBinaryExpCG node, String orResultVarName)
	{
		SExpCG left = node.getLeft().clone();
		SExpCG right = node.getRight().clone();
		
		AIfStmCG leftCheck = new AIfStmCG();
		leftCheck.setIfExp(left);
		
		AAssignToExpStmCG setOrResultVarTrue = new AAssignToExpStmCG();
		setOrResultVarTrue.setTarget(transformationAssistant.consBoolCheck(orResultVarName, false));
		setOrResultVarTrue.setExp(info.getAssistantManager().getExpAssistant().consBoolLiteral(true));
		
		leftCheck.setThenStm(setOrResultVarTrue);

		AAssignToExpStmCG setOrResultVarToRightExp = new AAssignToExpStmCG();
		setOrResultVarToRightExp.setTarget(transformationAssistant.consBoolCheck(orResultVarName, false));
		setOrResultVarToRightExp.setExp(right);
		
		leftCheck.setElseStm(setOrResultVarToRightExp);
		
		return leftCheck;
	}
	
	private boolean transformBoolBinaryExp(SBoolBinaryExpCG node, SStmCG enclosingStm)
	{
		// First condition: The enclosing statement can be 'null' if we only try to code generate an expression rather than
		// a complete specification.
		
		return enclosingStm != null && !transformationAssistant.getInfo().getExpAssistant().isLoopCondition(node);
	}

	private void visitBoolBinary(SBoolBinaryExpCG node) throws AnalysisException
	{
		node.getLeft().apply(this);
		node.getRight().apply(this);
		node.getType().apply(this);
	}

	private void handleLogicExp(SBoolBinaryExpCG node, SStmCG enclosingStm, SStmCG checkBlock, String resultName)
			throws AnalysisException
	{
		AVarDeclCG andResultDecl = transformationAssistant.consBoolVarDecl(resultName, false);
		
		ABlockStmCG declBlock = new ABlockStmCG();
		declBlock.getLocalDefs().add(andResultDecl);
		
		ABlockStmCG replacementBlock = new ABlockStmCG();

		transformationAssistant.replaceNodeWith(enclosingStm, replacementBlock);
		transformationAssistant.replaceNodeWith(node, transformationAssistant.consBoolCheck(resultName, false));
		
		replacementBlock.getStatements().add(declBlock);
		replacementBlock.getStatements().add(checkBlock);
		replacementBlock.getStatements().add(enclosingStm);
		
		replacementBlock.apply(this);
	}
	
	private List<ASetMultipleBindCG> filterMultipleBinds(SQuantifierExpCG node)
	{
		List<ASetMultipleBindCG> multipleSetBinds = new LinkedList<ASetMultipleBindCG>();
		
		for (SMultipleBindCG b : node.getBindList()){
			
			if(b instanceof ASetMultipleBindCG)
			{
				multipleSetBinds.add((ASetMultipleBindCG) b.clone());
			}
			else
			{
				info.addTransformationWarning(node, "Transformation only works for quantified "
						+ "expressions with multiple set binds and not multiple "
						+ "type binds in '" + this.getClass().getSimpleName() + "'");
			}
		}
		return multipleSetBinds;
	}
}
