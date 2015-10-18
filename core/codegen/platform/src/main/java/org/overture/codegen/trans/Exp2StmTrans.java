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

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
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
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACaseAltStmStmCG;
import org.overture.codegen.cgast.statements.ACasesStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.cgast.utils.AHeaderLetBeStCG;
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

public class Exp2StmTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;
	private ILanguageIterator langIterator;

	private Exists1CounterData counterData;
	private Exp2StmVarPrefixes prefixes;
	private IterationVarPrefixes iteVarPrefixes;

	public Exp2StmTrans(IterationVarPrefixes iteVarPrefixes,
			TransAssistantCG transAssistant, Exists1CounterData counterData,
			ILanguageIterator langIterator, Exp2StmVarPrefixes prefixes)
	{
		this.transAssistant = transAssistant;
		this.counterData = counterData;
		this.langIterator = langIterator;
		this.prefixes = prefixes;
		this.iteVarPrefixes = iteVarPrefixes;
	}

	@Override
	public void caseATernaryIfExpCG(ATernaryIfExpCG node)
			throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.findEnclosingStm(node);

		if (enclosingStm == null)
		{
			// TODO:
			// Cases such as
			// values
			// public x = 1 + if 2 = 3 then 4 else 5 + 6;
			// Will not be treated
			return;
		}

		String resultVarName = transAssistant.getInfo().getTempVarNameGen().nextVarName(prefixes.ternaryIfExp());

		AVarDeclCG resultDecl = transAssistant.consDecl(resultVarName, node.getType().clone(), transAssistant.getInfo().getExpAssistant().consNullExp());
		AIdentifierVarExpCG resultVar = transAssistant.getInfo().getExpAssistant().consIdVar(resultVarName, resultDecl.getType().clone());

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

		transAssistant.replaceNodeWith(node, resultVar);
		transAssistant.replaceNodeWith(enclosingStm, replacementBlock);

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
		// orResult = true;
		// }
		// else
		// {
		// orResult = right;
		// }
		//

		SStmCG enclosingStm = transAssistant.findEnclosingStm(node);

		if (transformBoolBinaryExp(node, enclosingStm))
		{
			String resultName = transAssistant.getInfo().getTempVarNameGen().nextVarName(prefixes.orExp());
			handleLogicExp(node, enclosingStm, consOrExpCheck(node, resultName), resultName);
		} else
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
		// if (right)
		// {
		// andResult = true;
		// }
		// }

		SStmCG enclosingStm = transAssistant.findEnclosingStm(node);

		if (transformBoolBinaryExp(node, enclosingStm))
		{
			String resultName = transAssistant.getInfo().getTempVarNameGen().nextVarName(prefixes.andExp());
			handleLogicExp(node, enclosingStm, consAndExpCheck(node, resultName), resultName);
		} else
		{
			visitBoolBinary(node);
		}
	}

	@Override
	public void caseALetBeStExpCG(ALetBeStExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "let be st expressions");

		AHeaderLetBeStCG header = node.getHeader();

		if (!(header.getBinding() instanceof ASetMultipleBindCG))
		{
			transAssistant.getInfo().addTransformationWarning(node.getHeader().getBinding(), "This transformation only works for 'let be st' "
					+ "expressions with with multiple set binds and not multiple type binds in '"
					+ this.getClass().getSimpleName() + "'");
			return;
		}

		ASetMultipleBindCG binding = (ASetMultipleBindCG) header.getBinding();
		SExpCG suchThat = header.getSuchThat();
		SSetTypeCG setType = transAssistant.getSetTypeCloned(binding.getSet());
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();

		LetBeStStrategy strategy = new LetBeStStrategy(transAssistant, suchThat, setType, langIterator, tempVarNameGen, iteVarPrefixes);

		ABlockStmCG outerBlock = new ABlockStmCG();

		SExpCG letBeStResult = null;

		if (transAssistant.hasEmptySet(binding))
		{
			transAssistant.cleanUpBinding(binding);
			letBeStResult = transAssistant.getInfo().getExpAssistant().consNullExp();
		} else
		{
			String var = tempVarNameGen.nextVarName(prefixes.letBeSt());
			SExpCG value = node.getValue();

			AVarDeclCG resultDecl = transAssistant.consDecl(var, value.getType().clone(), transAssistant.getInfo().getExpAssistant().consUndefinedExp());
			outerBlock.getLocalDefs().add(resultDecl);

			AAssignToExpStmCG setLetBeStResult = new AAssignToExpStmCG();
			setLetBeStResult.setTarget(transAssistant.getInfo().getExpAssistant().consIdVar(var, value.getType().clone()));
			setLetBeStResult.setExp(value);
			outerBlock.getStatements().add(setLetBeStResult);

			AIdentifierVarExpCG varExpResult = new AIdentifierVarExpCG();
			varExpResult.setType(value.getType().clone());
			varExpResult.setIsLocal(true);
			varExpResult.setName(var);
			letBeStResult = varExpResult;
		}

		// Replace the let be st expression with the result expression
		transAssistant.replaceNodeWith(node, letBeStResult);

		LinkedList<SPatternCG> patterns = binding.getPatterns();
		ABlockStmCG block = transAssistant.consIterationBlock(patterns, binding.getSet(), tempVarNameGen, strategy, iteVarPrefixes);
		outerBlock.getStatements().addFirst(block);

		// Replace the enclosing statement with the transformation
		transAssistant.replaceNodeWith(enclosingStm, outerBlock);

		// And make sure to have the enclosing statement in the transformed tree
		outerBlock.getStatements().add(enclosingStm);
		outerBlock.apply(this);

		outerBlock.setScoped(transAssistant.getInfo().getStmAssistant().isScoped(outerBlock));
	}

	@Override
	public void caseARecordModExpCG(ARecordModExpCG node)
			throws AnalysisException
	{
		String recModifierName = transAssistant.getInfo().getTempVarNameGen().nextVarName(prefixes.recModExp());

		AVarDeclCG recDecl = transAssistant.consDecl(recModifierName, node.getType().clone(), node.getRec().clone());
		ABlockStmCG declStm = new ABlockStmCG();
		declStm.getLocalDefs().add(recDecl);

		AIdentifierVarExpCG recVar = transAssistant.getInfo().getExpAssistant().consIdVar(recModifierName, node.getType().clone());

		ABlockStmCG replacementBlock = new ABlockStmCG();
		replacementBlock.getStatements().add(declStm);

		for (ARecordModifierCG modifier : node.getModifiers())
		{
			String name = modifier.getName();
			SExpCG value = modifier.getValue().clone();

			STypeCG fieldType = transAssistant.getInfo().getTypeAssistant().getFieldType(transAssistant.getInfo().getClasses(), node.getRecType(), name);

			AFieldExpCG field = new AFieldExpCG();
			field.setType(fieldType);
			field.setObject(recVar.clone());
			field.setMemberName(name);

			AAssignToExpStmCG assignment = new AAssignToExpStmCG();
			assignment.setTarget(field);
			assignment.setExp(value);

			replacementBlock.getStatements().add(assignment);
		}

		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "record modification expression");

		transform(enclosingStm, replacementBlock, recVar.clone(), node);

		replacementBlock.apply(this);
	}

	@Override
	public void caseACompMapExpCG(ACompMapExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "map comprehension");

		AMapletExpCG first = node.getFirst();
		SExpCG predicate = node.getPredicate();
		STypeCG type = node.getType();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.mapComp());

		ComplexCompStrategy strategy = new MapCompStrategy(transAssistant, first, predicate, var, type, langIterator, tempVarNameGen, iteVarPrefixes);

		List<ASetMultipleBindCG> bindings = filterBindList(node, node.getBindings());

		ABlockStmCG block = transAssistant.consComplexCompIterationBlock(bindings, tempVarNameGen, strategy, iteVarPrefixes);

		if (block.getStatements().isEmpty())
		{
			// In case the block has no statements the result of the map comprehension is the empty map
			AEnumMapExpCG emptyMap = new AEnumMapExpCG();
			emptyMap.setType(type.clone());

			// Replace the map comprehension with the empty map
			transAssistant.replaceNodeWith(node, emptyMap);
		} else
		{
			replaceCompWithTransformation(enclosingStm, block, type, var, node);
		}

		block.apply(this);
	}

	@Override
	public void caseACompSetExpCG(ACompSetExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "set comprehension");

		SExpCG first = node.getFirst();
		SExpCG predicate = node.getPredicate();
		STypeCG type = node.getType();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.setComp());

		ComplexCompStrategy strategy = new SetCompStrategy(transAssistant, first, predicate, var, type, langIterator, tempVarNameGen, iteVarPrefixes);

		List<ASetMultipleBindCG> bindings = filterBindList(node, node.getBindings());
		ABlockStmCG block = transAssistant.consComplexCompIterationBlock(bindings, tempVarNameGen, strategy, iteVarPrefixes);

		if (block.getStatements().isEmpty())
		{
			// In case the block has no statements the result of the set comprehension is the empty set
			AEnumSetExpCG emptySet = new AEnumSetExpCG();
			emptySet.setType(type.clone());

			// Replace the set comprehension with the empty set
			transAssistant.replaceNodeWith(node, emptySet);
		} else
		{
			replaceCompWithTransformation(enclosingStm, block, type, var, node);
		}

		block.apply(this);
	}

	@Override
	public void caseACompSeqExpCG(ACompSeqExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "sequence comprehension");

		SExpCG first = node.getFirst();
		SExpCG predicate = node.getPredicate();
		STypeCG type = node.getType();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.seqComp());

		SeqCompStrategy strategy = new SeqCompStrategy(transAssistant, first, predicate, var, type, langIterator, tempVarNameGen, iteVarPrefixes);

		if (transAssistant.isEmptySet(node.getSet()))
		{
			// In case the block has no statements the result of the sequence comprehension is the empty sequence
			AEnumSeqExpCG emptySeq = new AEnumSeqExpCG();
			emptySeq.setType(type.clone());

			// Replace the sequence comprehension with the empty sequence
			transAssistant.replaceNodeWith(node, emptySeq);
		} else
		{
			LinkedList<SPatternCG> patterns = new LinkedList<SPatternCG>();
			patterns.add(node.getSetBind().getPattern().clone());

			ABlockStmCG block = transAssistant.consIterationBlock(patterns, node.getSet(), transAssistant.getInfo().getTempVarNameGen(), strategy, iteVarPrefixes);

			replaceCompWithTransformation(enclosingStm, block, type, var, node);

			block.apply(this);
		}
	}

	@Override
	public void caseAForAllQuantifierExpCG(AForAllQuantifierExpCG node)
			throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "forall expression");

		SExpCG predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.forAll());

		OrdinaryQuantifierStrategy strategy = new OrdinaryQuantifierStrategy(transAssistant, predicate, var, OrdinaryQuantifier.FORALL, langIterator, tempVarNameGen, iteVarPrefixes);

		List<ASetMultipleBindCG> multipleSetBinds = filterBindList(node, node.getBindList());

		ABlockStmCG block = transAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy, iteVarPrefixes);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpCG forAllResult = transAssistant.getInfo().getExpAssistant().consBoolLiteral(true);
			transAssistant.replaceNodeWith(node, forAllResult);
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
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "exists expression");

		SExpCG predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.exists());

		OrdinaryQuantifierStrategy strategy = new OrdinaryQuantifierStrategy(transAssistant, predicate, var, OrdinaryQuantifier.EXISTS, langIterator, tempVarNameGen, iteVarPrefixes);

		List<ASetMultipleBindCG> multipleSetBinds = filterBindList(node, node.getBindList());

		ABlockStmCG block = transAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy, iteVarPrefixes);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpCG existsResult = transAssistant.getInfo().getExpAssistant().consBoolLiteral(false);
			transAssistant.replaceNodeWith(node, existsResult);
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
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "exists1 expression");

		SExpCG predicate = node.getPredicate();
		ITempVarGen tempVarNameGen = transAssistant.getInfo().getTempVarNameGen();
		String var = tempVarNameGen.nextVarName(prefixes.exists1());

		Exists1QuantifierStrategy strategy = new Exists1QuantifierStrategy(transAssistant, predicate, var, langIterator, tempVarNameGen, iteVarPrefixes, counterData);

		List<ASetMultipleBindCG> multipleSetBinds = filterBindList(node, node.getBindList());

		ABlockStmCG block = transAssistant.consComplexCompIterationBlock(multipleSetBinds, tempVarNameGen, strategy, iteVarPrefixes);

		if (multipleSetBinds.isEmpty())
		{
			ABoolLiteralExpCG exists1Result = transAssistant.getInfo().getExpAssistant().consBoolLiteral(false);
			transAssistant.replaceNodeWith(node, exists1Result);
		} else
		{
			AIdentifierVarExpCG counter = new AIdentifierVarExpCG();
			counter.setType(new AIntNumericBasicTypeCG());
			counter.setIsLocal(true);
			counter.setName(var);

			AEqualsBinaryExpCG exists1Result = new AEqualsBinaryExpCG();
			exists1Result.setType(new ABoolBasicTypeCG());
			exists1Result.setLeft(counter);
			exists1Result.setRight(transAssistant.getInfo().getExpAssistant().consIntLiteral(1));

			transform(enclosingStm, block, exists1Result, node);
			block.apply(this);
		}
	}

	public void caseALetDefExpCG(ALetDefExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "let def expression");

		SExpCG exp = node.getExp();
		transAssistant.replaceNodeWith(node, exp);

		ABlockStmCG topBlock = new ABlockStmCG();
		ABlockStmCG current = topBlock;

		for (AVarDeclCG local : node.getLocalDefs())
		{
			ABlockStmCG tmp = new ABlockStmCG();
			tmp.getLocalDefs().add(local.clone());
			current.getStatements().add(tmp);
			current = tmp;
		}

		transAssistant.replaceNodeWith(enclosingStm, topBlock);
		topBlock.getStatements().add(enclosingStm);

		exp.apply(this);
		topBlock.apply(this);

		topBlock.setScoped(transAssistant.getInfo().getStmAssistant().isScoped(topBlock));
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
		transAssistant.replaceNodeWith(node, nodeResult);

		// Replace the enclosing statement with the transformation
		transAssistant.replaceNodeWith(enclosingStm, block);

		// And make sure to have the enclosing statement in the transformed tree
		block.getStatements().add(enclosingStm);
	}

	private AAssignToExpStmCG assignToVar(AIdentifierVarExpCG var, SExpCG exp)
	{
		AAssignToExpStmCG assignment = new AAssignToExpStmCG();
		assignment.setTarget(var.clone());
		assignment.setExp(exp.clone());

		return assignment;
	}

	@Override
	public void caseACasesExpCG(ACasesExpCG node) throws AnalysisException
	{
		SStmCG enclosingStm = transAssistant.getEnclosingStm(node, "cases expression");

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		String casesExpResultName = transAssistant.getInfo().getTempVarNameGen().nextVarName(prefixes.casesExp());
		idPattern.setName(casesExpResultName);

		AVarDeclCG resultVarDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(node.getType().clone(), idPattern, new AUndefinedExpCG());

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

	private AIfStmCG consAndExpCheck(AAndBoolBinaryExpCG node,
			String andResultVarName)
	{
		SExpCG left = node.getLeft().clone();
		SExpCG right = node.getRight().clone();

		AIfStmCG leftCheck = new AIfStmCG();
		leftCheck.setIfExp(left);

		AIfStmCG rightCheck = new AIfStmCG();
		rightCheck.setIfExp(right);

		AAssignToExpStmCG assignAndVar = new AAssignToExpStmCG();
		assignAndVar.setTarget(transAssistant.consBoolCheck(andResultVarName, false));
		assignAndVar.setExp(transAssistant.getInfo().getAssistantManager().getExpAssistant().consBoolLiteral(true));

		rightCheck.setThenStm(assignAndVar);

		leftCheck.setThenStm(rightCheck);

		return leftCheck;
	}

	private SStmCG consOrExpCheck(AOrBoolBinaryExpCG node,
			String orResultVarName)
	{
		SExpCG left = node.getLeft().clone();
		SExpCG right = node.getRight().clone();

		AIfStmCG leftCheck = new AIfStmCG();
		leftCheck.setIfExp(left);

		AAssignToExpStmCG setOrResultVarTrue = new AAssignToExpStmCG();
		setOrResultVarTrue.setTarget(transAssistant.consBoolCheck(orResultVarName, false));
		setOrResultVarTrue.setExp(transAssistant.getInfo().getAssistantManager().getExpAssistant().consBoolLiteral(true));

		leftCheck.setThenStm(setOrResultVarTrue);

		AAssignToExpStmCG setOrResultVarToRightExp = new AAssignToExpStmCG();
		setOrResultVarToRightExp.setTarget(transAssistant.consBoolCheck(orResultVarName, false));
		setOrResultVarToRightExp.setExp(right);

		leftCheck.setElseStm(setOrResultVarToRightExp);

		return leftCheck;
	}

	private boolean transformBoolBinaryExp(SBoolBinaryExpCG node,
			SStmCG enclosingStm)
	{
		// First condition: The enclosing statement can be 'null' if we only try to code generate an expression rather
		// than
		// a complete specification.

		return enclosingStm != null
				&& !transAssistant.getInfo().getExpAssistant().isLoopCondition(node);
	}

	private void visitBoolBinary(SBoolBinaryExpCG node)
			throws AnalysisException
	{
		node.getLeft().apply(this);
		node.getRight().apply(this);
		node.getType().apply(this);
	}

	private void handleLogicExp(SBoolBinaryExpCG node, SStmCG enclosingStm,
			SStmCG checkBlock, String resultName) throws AnalysisException
	{
		AVarDeclCG andResultDecl = transAssistant.consBoolVarDecl(resultName, false);

		ABlockStmCG declBlock = new ABlockStmCG();
		declBlock.getLocalDefs().add(andResultDecl);

		ABlockStmCG replacementBlock = new ABlockStmCG();

		transAssistant.replaceNodeWith(enclosingStm, replacementBlock);
		transAssistant.replaceNodeWith(node, transAssistant.consBoolCheck(resultName, false));

		replacementBlock.getStatements().add(declBlock);
		replacementBlock.getStatements().add(checkBlock);
		replacementBlock.getStatements().add(enclosingStm);

		replacementBlock.apply(this);
	}

	private List<ASetMultipleBindCG> filterBindList(INode node,
			LinkedList<SMultipleBindCG> bindList)
	{
		List<ASetMultipleBindCG> multipleSetBinds = new LinkedList<ASetMultipleBindCG>();

		for (SMultipleBindCG b : bindList)
		{

			if (b instanceof ASetMultipleBindCG)
			{
				multipleSetBinds.add((ASetMultipleBindCG) b.clone());
			} else
			{
				transAssistant.getInfo().addTransformationWarning(node, "Transformation only works for "
						+ "expressions with multiple set binds and not multiple "
						+ "type binds in '"
						+ this.getClass().getSimpleName()
						+ "'");
			}
		}

		return multipleSetBinds;
	}
}
