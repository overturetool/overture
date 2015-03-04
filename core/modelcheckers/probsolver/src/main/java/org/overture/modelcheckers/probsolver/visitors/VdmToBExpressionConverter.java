/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.modelcheckers.probsolver.visitors;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.ast.expressions.AAbsoluteUnaryExp;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.ACardinalityUnaryExp;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.ADistConcatUnaryExp;
import org.overture.ast.expressions.ADistIntersectUnaryExp;
import org.overture.ast.expressions.ADistUnionUnaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.ADomainResByBinaryExp;
import org.overture.ast.expressions.ADomainResToBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AHeadUnaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.AIndicesUnaryExp;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ALenUnaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AMapDomainUnaryExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapInverseUnaryExp;
import org.overture.ast.expressions.AMapRangeUnaryExp;
import org.overture.ast.expressions.AMapUnionBinaryExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.ANotInSetBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.APlusPlusBinaryExp;
import org.overture.ast.expressions.APowerSetUnaryExp;
import org.overture.ast.expressions.AProperSubsetBinaryExp;
import org.overture.ast.expressions.ARangeResByBinaryExp;
import org.overture.ast.expressions.ARangeResToBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.AReverseUnaryExp;
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
import org.overture.ast.expressions.ASubsetBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATailUnaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.ATokenBasicType;

import de.be4.classicalb.core.parser.node.AAddExpression;
import de.be4.classicalb.core.parser.node.ABooleanFalseExpression;
import de.be4.classicalb.core.parser.node.ABooleanTrueExpression;
import de.be4.classicalb.core.parser.node.ACardExpression;
import de.be4.classicalb.core.parser.node.ACompositionExpression;
import de.be4.classicalb.core.parser.node.AComprehensionSetExpression;
import de.be4.classicalb.core.parser.node.AConcatExpression;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.AConvertBoolExpression;
import de.be4.classicalb.core.parser.node.ACoupleExpression;
import de.be4.classicalb.core.parser.node.ADisjunctPredicate;
import de.be4.classicalb.core.parser.node.ADivExpression;
import de.be4.classicalb.core.parser.node.ADomainExpression;
import de.be4.classicalb.core.parser.node.ADomainRestrictionExpression;
import de.be4.classicalb.core.parser.node.ADomainSubtractionExpression;
import de.be4.classicalb.core.parser.node.AEmptySequenceExpression;
import de.be4.classicalb.core.parser.node.AEmptySetExpression;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AEquivalencePredicate;
import de.be4.classicalb.core.parser.node.AExistsPredicate;
import de.be4.classicalb.core.parser.node.AFalsityPredicate;
import de.be4.classicalb.core.parser.node.AFirstExpression;
import de.be4.classicalb.core.parser.node.AForallPredicate;
import de.be4.classicalb.core.parser.node.AFunctionExpression;
import de.be4.classicalb.core.parser.node.AGeneralConcatExpression;
import de.be4.classicalb.core.parser.node.AGeneralIntersectionExpression;
import de.be4.classicalb.core.parser.node.AGeneralUnionExpression;
import de.be4.classicalb.core.parser.node.AGreaterEqualPredicate;
import de.be4.classicalb.core.parser.node.AGreaterPredicate;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AImplicationPredicate;
import de.be4.classicalb.core.parser.node.AIntegerExpression;
import de.be4.classicalb.core.parser.node.AIntersectionExpression;
import de.be4.classicalb.core.parser.node.AIntervalExpression;
import de.be4.classicalb.core.parser.node.AIterationExpression;
import de.be4.classicalb.core.parser.node.ALessEqualPredicate;
import de.be4.classicalb.core.parser.node.ALessPredicate;
import de.be4.classicalb.core.parser.node.AMaxExpression;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.AMinusOrSetSubtractExpression;
import de.be4.classicalb.core.parser.node.AModuloExpression;
import de.be4.classicalb.core.parser.node.AMultiplicationExpression;
import de.be4.classicalb.core.parser.node.ANegationPredicate;
import de.be4.classicalb.core.parser.node.ANotEqualPredicate;
import de.be4.classicalb.core.parser.node.ANotMemberPredicate;
import de.be4.classicalb.core.parser.node.AOverwriteExpression;
import de.be4.classicalb.core.parser.node.APowSubsetExpression;
import de.be4.classicalb.core.parser.node.APowerOfExpression;
import de.be4.classicalb.core.parser.node.ARangeExpression;
import de.be4.classicalb.core.parser.node.ARangeRestrictionExpression;
import de.be4.classicalb.core.parser.node.ARangeSubtractionExpression;
import de.be4.classicalb.core.parser.node.ARecordFieldExpression;
import de.be4.classicalb.core.parser.node.ARevExpression;
import de.be4.classicalb.core.parser.node.AReverseExpression;
import de.be4.classicalb.core.parser.node.ASequenceExtensionExpression;
import de.be4.classicalb.core.parser.node.ASetExtensionExpression;
import de.be4.classicalb.core.parser.node.ASizeExpression;
import de.be4.classicalb.core.parser.node.AStringExpression;
import de.be4.classicalb.core.parser.node.ASubsetPredicate;
import de.be4.classicalb.core.parser.node.ASubsetStrictPredicate;
import de.be4.classicalb.core.parser.node.ATailExpression;
import de.be4.classicalb.core.parser.node.AUnaryMinusExpression;
import de.be4.classicalb.core.parser.node.AUnionExpression;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.be4.classicalb.core.parser.node.TIntegerLiteral;
import de.be4.classicalb.core.parser.node.TStringLiteral;

public abstract class VdmToBExpressionConverter extends
		DepthFirstAnalysisAdaptorAnswer<Node>
{
	/**
	 * Utility method to translate to an expression
	 * 
	 * @param n
	 * @return
	 * @throws AnalysisException
	 */
	public PExpression exp(INode n) throws AnalysisException
	{
		Node result = n.apply(this);
		if (result instanceof PPredicate)
		{
			result = new AConvertBoolExpression((PPredicate) result);
		}

		return (PExpression) result;
	}

	/**
	 * Utility method to translate to a predicate
	 * 
	 * @param n
	 * @return
	 * @throws AnalysisException
	 */
	protected PPredicate pred(INode n) throws AnalysisException
	{
		Node result = n.apply(this);
		if (result instanceof PExpression)
		{
			if (result instanceof PExpression)
			{

				if (result instanceof ABooleanTrueExpression)
				{
					// TODO clean this later
					return new AEqualPredicate(new ABooleanTrueExpression(), new ABooleanTrueExpression());// new
																											// ATruthPredicate();
				} else if (result instanceof ABooleanFalseExpression)
				{
					return new AFalsityPredicate();
				}
			}
		}

		return (PPredicate) result;
	}

	/**
	 * Construct an identifier from a string using the recorded substitution rules
	 * 
	 * @param n
	 * @return
	 */
	protected abstract PExpression getIdentifier(String n);

	@Override
	public Node caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node)
			throws AnalysisException
	{

		return new AConjunctPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseAEqualsBinaryExp(AEqualsBinaryExp node)
			throws AnalysisException
	{
		return new AEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASetUnionBinaryExp(ASetUnionBinaryExp node)
			throws AnalysisException
	{
		return new AUnionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAInSetBinaryExp(AInSetBinaryExp node)
			throws AnalysisException
	{
		return new AMemberPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASubsetBinaryExp(ASubsetBinaryExp node)
			throws AnalysisException
	{
		return new ASubsetPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseACardinalityUnaryExp(ACardinalityUnaryExp node)
			throws AnalysisException
	{
		return new ACardExpression(exp(node.getExp()));
	}

	@Override
	public Node caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node)
			throws AnalysisException
	{
		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAFieldExp(AFieldExp node) throws AnalysisException
	{
		return new ARecordFieldExpression(exp(node.getObject()), getIdentifier(node.getField().getName()));
	}

	@Override
	public Node caseASetEnumSetExp(ASetEnumSetExp node)
			throws AnalysisException
	{
		if (node.getMembers().isEmpty())
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression set = new ASetExtensionExpression();
		for (PExp m : node.getMembers())
		{
			set.getExpressions().add(exp(m));
		}

		return set;
	}

	@Override
	public Node caseAOrBooleanBinaryExp(AOrBooleanBinaryExp node)
			throws AnalysisException
	{
		return new ADisjunctPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseANotUnaryExp(ANotUnaryExp node) throws AnalysisException
	{
		return new ANegationPredicate(pred(node.getExp()));
	}

	@Override
	public Node caseABooleanConstExp(ABooleanConstExp node)
			throws AnalysisException
	{

		if (node.getValue().getValue())
		{
			return new ABooleanTrueExpression();
		} else
		{
			return new ABooleanFalseExpression();
		}
	}

	@Override
	public Node caseAPlusNumericBinaryExp(APlusNumericBinaryExp node)
			throws AnalysisException
	{

		return new AAddExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node)
			throws AnalysisException
	{

		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseATimesNumericBinaryExp(ATimesNumericBinaryExp node)
			throws AnalysisException
	{

		return new AMultiplicationExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADivideNumericBinaryExp(ADivideNumericBinaryExp node)
			throws AnalysisException
	{

		return new ADivExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADivNumericBinaryExp(ADivNumericBinaryExp node)
			throws AnalysisException
	{
		// x div y = x / y
		return new ADivExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARemNumericBinaryExp(ARemNumericBinaryExp node)
			throws AnalysisException
	{
		// x rem y = x - y * (x/y)
		return new AMinusOrSetSubtractExpression(exp(node.getLeft()), new AMultiplicationExpression(exp(node.getRight()), new ADivExpression(exp(node.getLeft()), exp(node.getRight()))));
	}

	@Override
	public Node caseAModNumericBinaryExp(AModNumericBinaryExp node)
			throws AnalysisException
	{

		return new AModuloExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node)
			throws AnalysisException
	{

		return new AUnaryMinusExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node)
			throws AnalysisException
	{

		PExp num = node.getExp();
		ASetExtensionExpression nums = new ASetExtensionExpression();
		nums.getExpressions().add(exp(num));
		nums.getExpressions().add(new AUnaryMinusExpression(exp(num)));
		return new AMaxExpression(nums);
	}

	@Override
	public Node caseAStarStarBinaryExp(AStarStarBinaryExp node)
			throws AnalysisException
	{
		if (node.getLeft().getType().toString().indexOf("map") == 0)
		{
			// for map
			return new AIterationExpression(exp(node.getLeft()), exp(node.getRight()));
		} else
		{
			// for numeric
			return new APowerOfExpression(exp(node.getLeft()), exp(node.getRight()));
		}
	}

	@Override
	public Node caseALessNumericBinaryExp(ALessNumericBinaryExp node)
			throws AnalysisException
	{
		return new ALessPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseALessEqualNumericBinaryExp(ALessEqualNumericBinaryExp node)
			throws AnalysisException
	{
		return new ALessEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node)
			throws AnalysisException
	{
		return new AGreaterPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node) throws AnalysisException
	{
		return new AGreaterEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASetIntersectBinaryExp(ASetIntersectBinaryExp node)
			throws AnalysisException
	{
		return new AIntersectionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADistUnionUnaryExp(ADistUnionUnaryExp node)
			throws AnalysisException
	{
		return new AGeneralUnionExpression(exp(node.getExp()));
	}

	@Override
	public Node caseADistIntersectUnaryExp(ADistIntersectUnaryExp node)
			throws AnalysisException
	{
		return new AGeneralIntersectionExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAPowerSetUnaryExp(APowerSetUnaryExp node)
			throws AnalysisException
	{
		return new APowSubsetExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAImpliesBooleanBinaryExp(AImpliesBooleanBinaryExp node)
			throws AnalysisException
	{
		return new AImplicationPredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseANotInSetBinaryExp(ANotInSetBinaryExp node)
			throws AnalysisException
	{
		return new ANotMemberPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node)
			throws AnalysisException
	{
		return new ASubsetStrictPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseANotEqualBinaryExp(ANotEqualBinaryExp node)
			throws AnalysisException
	{
		return new ANotEqualPredicate(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseASeqEnumSeqExp(ASeqEnumSeqExp node)
			throws AnalysisException
	{
		if (node.getMembers().isEmpty())
		{
			return new AEmptySequenceExpression();
		}

		ASequenceExtensionExpression seq = new ASequenceExtensionExpression();
		for (PExp m : node.getMembers())
		{
			seq.getExpression().add(exp(m));
		}
		return seq;
	}

	@Override
	public Node caseAHeadUnaryExp(AHeadUnaryExp node) throws AnalysisException
	{
		return new AFirstExpression(exp(node.getExp()));
	}

	@Override
	public Node caseATailUnaryExp(ATailUnaryExp node) throws AnalysisException
	{
		return new ATailExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAIndicesUnaryExp(AIndicesUnaryExp node)
			throws AnalysisException
	{
		LinkedList<PExp> seqmem = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		String size = new String(new Integer(seqmem.size()).toString());
		return new AIntervalExpression(new AIntegerExpression(new TIntegerLiteral("1")), new AIntegerExpression(new TIntegerLiteral(size)));
	}

	@Override
	public Node caseALenUnaryExp(ALenUnaryExp node) throws AnalysisException
	{
		return new ASizeExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAReverseUnaryExp(AReverseUnaryExp node) // not yet checked
			throws AnalysisException
	{
		return new ARevExpression(exp(node.getExp()));
	}

	@Override
	public Node caseASeqConcatBinaryExp(ASeqConcatBinaryExp node)
			throws AnalysisException
	{
		return new AConcatExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAApplyExp(AApplyExp node) throws AnalysisException
	{
		List<PExpression> args = new Vector<PExpression>();

		for (PExp arg : node.getArgs())
		{
			args.add(exp(arg));
		}

		return new AFunctionExpression(exp(node.getRoot()), args);
	}

	@Override
	public Node caseACompBinaryExp(ACompBinaryExp node)
			throws AnalysisException
	{
		return new ACompositionExpression(exp(node.getRight()), exp(node.getLeft()));
	}

	@Override
	public Node caseAMapInverseUnaryExp(AMapInverseUnaryExp node)
			throws AnalysisException
	{
		return new AReverseExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAForAllExp(AForAllExp node) throws AnalysisException
	{
		AForallPredicate fap = new AForallPredicate();
		LinkedList<PMultipleBind> blist = node.getBindList();

		fap.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		fap.setImplication(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));
		if (blist.size() > 1)
		{
			for (int i = 1; i < blist.size(); i++)
			{
				for (int j = 0; j < blist.get(i).getPlist().size(); j++)
				{
					fap.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
					fap.setImplication(new AConjunctPredicate(fap.getImplication(), new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
				}
			}
		}

		fap.setImplication(new AImplicationPredicate(fap.getImplication(), pred(node.getPredicate())));

		return fap;
	}

	@Override
	public Node caseAExistsExp(AExistsExp node) throws AnalysisException
	{
		AExistsPredicate esp = new AExistsPredicate();
		LinkedList<PMultipleBind> blist = node.getBindList();

		esp.getIdentifiers().add(exp(blist.get(0).getPlist().get(0)));
		esp.setPredicate(new AMemberPredicate(exp(blist.get(0).getPlist().get(0)), exp(blist.get(0))));
		if (blist.size() > 1)
		{
			for (int i = 1; i < blist.size(); i++)
			{
				for (int j = 0; j < blist.get(i).getPlist().size(); j++)
				{
					esp.getIdentifiers().add(exp(blist.get(i).getPlist().get(j)));
					esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), new AMemberPredicate(exp(blist.get(i).getPlist().get(j)), exp(blist.get(i)))));
				}
			}
		}

		esp.setPredicate(new AConjunctPredicate(esp.getPredicate(), pred(node.getPredicate())));

		return esp;
	}

	@Override
	public Node caseAExists1Exp(AExists1Exp node) throws AnalysisException
	{
		// exists1 x in set S & pred -> card( { x | x : S & pred } ) = 1

		AIntegerExpression one = new AIntegerExpression(new TIntegerLiteral(new String(new Integer("1").toString())));
		AComprehensionSetExpression cse = new AComprehensionSetExpression();

		cse.setPredicates(new AMemberPredicate(exp(node.getBind().getPattern()), exp(((ASetBind) node.getBind()).getSet())));

		cse.getIdentifiers().add(exp(node.getBind().getPattern()));
		cse.setPredicates(new AConjunctPredicate(cse.getPredicates(), pred(node.getPredicate())));
		AEqualPredicate equal = new AEqualPredicate(new ACardExpression(cse), one);

		return equal;

	}

	@Override
	public Node caseAIdentifierPattern(AIdentifierPattern node)
			throws AnalysisException
	{
		AIdentifierExpression aie = new AIdentifierExpression();
		aie.getIdentifier().add(new TIdentifierLiteral(node.getName().toString()));
		return aie;
	}

	@Override
	public Node caseASetMultipleBind(ASetMultipleBind node)
			throws AnalysisException
	{
		if (node.getSet() instanceof ASetEnumSetExp)
		{
			ASetEnumSetExp setEnum = (ASetEnumSetExp) node.getSet();

			if (setEnum.getMembers().isEmpty())
			{
				return new AEmptySetExpression();
			}

			ASetExtensionExpression set = new ASetExtensionExpression();
			for (PExp m : setEnum.getMembers())
			{
				set.getExpressions().add(exp(m));
			}

			return set;
		} else if (node.getSet() instanceof ASetCompSetExp)
		{

		} else if (node.getSet() instanceof ASetRangeSetExp)
		{

		}

		// error case
		return super.caseASetMultipleBind(node);
	}

	@Override
	public Node caseAEquivalentBooleanBinaryExp(AEquivalentBooleanBinaryExp node)
			throws AnalysisException
	{
		return new AEquivalencePredicate(pred(node.getLeft()), pred(node.getRight()));
	}

	@Override
	public Node caseADistConcatUnaryExp(ADistConcatUnaryExp node)
			throws AnalysisException
	{

		LinkedList<PExp> seqlist = ((ASeqEnumSeqExp) node.getExp()).getMembers();
		if (seqlist.isEmpty())
		{
			return new AEmptySequenceExpression();
		}

		ASequenceExtensionExpression seq = new ASequenceExtensionExpression();
		for (PExp m : seqlist)
		{
			seq.getExpression().add(exp(m));
		}
		return new AGeneralConcatExpression(seq);
	}

	@Override
	public Node caseATupleExp(ATupleExp node) throws AnalysisException
	{
		LinkedList<PExp> args = node.getArgs();
		ACoupleExpression cpl = new ACoupleExpression();
		for (PExp elem : args)
		{
			cpl.getList().add(exp(elem));
		}
		return cpl;

	}
	
	@Override
	public Node caseAIntLiteralExp(AIntLiteralExp node)
			throws AnalysisException
	{
		return new AIntegerExpression(new TIntegerLiteral(""
				+ node.getValue().getValue()));
	}

	@Override
	public Node caseAMkBasicExp(AMkBasicExp node) throws AnalysisException
	{
		if (node.getType() instanceof ATokenBasicType)
		{
			return node.getArg().apply(this);
		}
		return super.caseAMkBasicExp(node);
	}
	
	@Override
	public Node caseAStringLiteralExp(AStringLiteralExp node)
			throws AnalysisException
	{
		return new AStringExpression(new TStringLiteral(node.getValue().getValue()));
	}

	/*
	 * Maps
	 */
	@Override
	public Node caseAMapletExp(AMapletExp node) throws AnalysisException
	{
		ACoupleExpression cpl = new ACoupleExpression();
		cpl.getList().add(exp(node.getLeft()));
		cpl.getList().add(exp(node.getRight()));
		return cpl;
	}

	@Override
	public Node caseAMapEnumMapExp(AMapEnumMapExp node)
			throws AnalysisException
	{
		if (node.getMembers().isEmpty())
		{
			return new AEmptySetExpression();
		}

		ASetExtensionExpression map = new ASetExtensionExpression();
		for (AMapletExp m : node.getMembers())
		{
			map.getExpressions().add(exp(m));
		}

		return map;
	}

	@Override
	public Node caseAMapDomainUnaryExp(AMapDomainUnaryExp node)
			throws AnalysisException
	{
		return new ADomainExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAMapRangeUnaryExp(AMapRangeUnaryExp node)
			throws AnalysisException
	{
		return new ARangeExpression(exp(node.getExp()));
	}

	@Override
	public Node caseAMapUnionBinaryExp(AMapUnionBinaryExp node)
			throws AnalysisException
	{
		return new AUnionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseAPlusPlusBinaryExp(APlusPlusBinaryExp node)
			throws AnalysisException
	{
		// seq ++ map
		// map ++ map
		return new AOverwriteExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADomainResToBinaryExp(ADomainResToBinaryExp node)
			throws AnalysisException
	{
		return new ADomainRestrictionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseADomainResByBinaryExp(ADomainResByBinaryExp node)
			throws AnalysisException
	{
		return new ADomainSubtractionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARangeResToBinaryExp(ARangeResToBinaryExp node)
			throws AnalysisException
	{
		return new ARangeRestrictionExpression(exp(node.getLeft()), exp(node.getRight()));
	}

	@Override
	public Node caseARangeResByBinaryExp(ARangeResByBinaryExp node)
			throws AnalysisException
	{
		return new ARangeSubtractionExpression(exp(node.getLeft()), exp(node.getRight()));
	}
}
