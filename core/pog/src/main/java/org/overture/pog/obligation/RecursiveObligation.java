/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.pog.obligation;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class RecursiveObligation extends ProofObligation
{

	private static final long serialVersionUID = -6975984943449362262L;

	private static final String LEFT_MEASURE_NAME = "LME";
	private static final String RIGHT_MEASURE_NAME = "RME";

	public RecursiveObligation(AExplicitFunctionDefinition def,
			AApplyExp apply, IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(apply, POType.RECURSIVE, ctxt, apply.getLocation(), af);

		PExp measureLeft_exp = buildMeasureLeft(def, apply);
		PExp measureRight_exp = buildMeasureRight(def, apply);

		PExp lt_exp = buildStructuralComparison(measureLeft_exp, measureRight_exp, getLex(def.getMeasureDef()));

		stitch = lt_exp;
		valuetree.setPredicate(ctxt.getPredWithContext(lt_exp));
	}

	public RecursiveObligation(AImplicitFunctionDefinition def,
			AApplyExp apply, IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.RECURSIVE, ctxt, apply.getLocation(), af);

		PExp measureLeft_exp = buildMeasureLeft(def, apply);
		PExp measureRight_exp = buildMeasureRight(def, apply);

		PExp lt_exp = buildStructuralComparison(measureLeft_exp, measureRight_exp, getLex(def.getMeasureDef()));

		stitch = lt_exp;
		valuetree.setPredicate(ctxt.getPredWithContext(lt_exp));
	}

	private PExp buildMeasureLeft(AExplicitFunctionDefinition def,
			AApplyExp apply) throws AnalysisException
	{
		List<PPattern> paramPatterns = new LinkedList<PPattern>();
		for (List<PPattern> list : def.getParamPatternList())
		{
			paramPatterns.addAll(list);
		}

		return buildMeasureLeftParams(apply, def.getTypeParams(), def.getActualResult(), def.getMeasureName(), paramPatterns);
	}

	private PExp buildMeasureLeft(AImplicitFunctionDefinition def,
			AApplyExp apply) throws AnalysisException
	{
		List<PPattern> paramPatterns = new LinkedList<PPattern>();
		for (APatternListTypePair pair : def.getParamPatterns())
		{
			paramPatterns.addAll(pair.getPatterns());
		}

		return buildMeasureLeftParams(apply, def.getTypeParams(), def.getActualResult(), def.getMeasureName(), paramPatterns);
	}

	private PExp buildMeasureLeftParams(AApplyExp apply,
			List<ILexNameToken> typeParams, PType actualResult,
			ILexNameToken measure, List<PPattern> paramPatterns)
			throws AnalysisException
	{
		AApplyExp apply_exp = new AApplyExp();

		if (typeParams != null && !typeParams.isEmpty())
		{
			if (apply.getRoot() instanceof AFuncInstatiationExp)
			{
				AFuncInstatiationExp func_exp = (AFuncInstatiationExp) apply.getRoot().clone();
				func_exp.setFunction(wrapName(measure.clone()));
				apply_exp.setRoot(func_exp);
			}
			else
			{
				AFuncInstatiationExp func_exp = new AFuncInstatiationExp();
				func_exp.setActualTypes(cloneListType(apply.getArgtypes()));	// Not sure about this?
				func_exp.setFunction(wrapName(measure.clone()));
				apply_exp.setRoot(func_exp);
			}
		}
		else
		{
			apply_exp.setRoot(wrapName(measure.clone()));
		}

		List<PExp> args = new LinkedList<PExp>();

		for (PPattern p : paramPatterns)
		{
			args.add(patternToExp(p.clone()));
		}

		apply_exp.setType(actualResult.clone());
		apply_exp.setArgs(args);
		return apply_exp;

	}

	private PExp buildMeasureRight(AExplicitFunctionDefinition def,
			AApplyExp apply)
	{
		return buildMeasureRightParams(apply, def.getMeasureName(), def.getActualResult());
	}

	private PExp buildMeasureRight(AImplicitFunctionDefinition def,
			AApplyExp apply)
	{
		return buildMeasureRightParams(apply, def.getMeasureName(), def.getActualResult());
	}

	private PExp buildMeasureRightParams(AApplyExp apply,
			ILexNameToken measure, PType actualResult)
	{
		PExp start = null;
		PExp root = apply.getRoot().clone();

		if (root instanceof AApplyExp)
		{
			AApplyExp aexp = (AApplyExp) root;
			start = buildMeasureRightParams(aexp, measure, actualResult);
		} else if (root instanceof AVariableExp)
		{
			start = wrapName(measure.clone());
		} else if (root instanceof AFuncInstatiationExp)
		{
			AFuncInstatiationExp fie = (AFuncInstatiationExp) root;
			AFuncInstatiationExp func_exp = new AFuncInstatiationExp();
			func_exp.setActualTypes(cloneListType(fie.getActualTypes()));
			func_exp.setFunction(wrapName(measure.clone()));
			start = func_exp;
		} else
		{
			start = root;
		}

		List<PExp> arglist = new LinkedList<PExp>();
		for (PExp arg : apply.getArgs())
		{
			arglist.add(arg.clone());
		}

		AApplyExp apply_exp = getApplyExp(start, arglist);
		apply_exp.setType(actualResult.clone());

		return apply_exp;
	}

	private PExp buildStructuralComparison(PExp left_exp, PExp right_exp,
			int measureLexical)
	{
		if (measureLexical == 0)
		// what about 1 measures? same as 0?
		{
			return AstExpressionFactory.newAGreaterNumericBinaryExp(left_exp, right_exp);
		}

		ALetDefExp let_exp = new ALetDefExp();

		AValueDefinition left_def = buildValueDef(left_exp, LEFT_MEASURE_NAME);
		AValueDefinition right_def = buildValueDef(right_exp, RIGHT_MEASURE_NAME);

		List<PDefinition> localDefs = new LinkedList<PDefinition>();
		localDefs.add(left_def);
		localDefs.add(right_def);

		let_exp.setLocalDefs(localDefs);

		// let left = [left expression], right=[right expression]
		// in ...

		// we don't strictly need the let in

		AVariableExp leftName_exp = wrapName(new LexNameToken(null, LEFT_MEASURE_NAME, null));
		AVariableExp rightName_exp = wrapName(new LexNameToken(null, RIGHT_MEASURE_NAME, null));

		// build the left < right structural comparison expression
		let_exp.setExpression(buildStructuralLessThan(leftName_exp, rightName_exp, 1, measureLexical).clone());

		return let_exp;
	}

	private PExp buildStructuralLessThan(PExp left_exp, PExp right_exp,
			int tupleCounter, int recCounter)
	{
		// left.i
		AFieldNumberExp leftField_exp = new AFieldNumberExp();
		leftField_exp.setTuple(left_exp.clone());
		leftField_exp.setField(new LexIntegerToken(tupleCounter, null));

		// right.i
		AFieldNumberExp rightField_exp = new AFieldNumberExp();
		rightField_exp.setTuple(right_exp.clone());
		rightField_exp.setField(new LexIntegerToken(tupleCounter, null));

		if (recCounter == 1)
		{
			// last one. don't chain further ifs
			return AstExpressionFactory.newAGreaterNumericBinaryExp(leftField_exp, rightField_exp);
		}

		// left.i <> right.i
		ANotEqualBinaryExp notEquals_exp = AstExpressionFactory.newANotEqualBinaryExp(leftField_exp, rightField_exp);

		// if left.i <>right.i then left.i , right.i else [recurse]
		AGreaterNumericBinaryExp gt_exp = AstExpressionFactory.newAGreaterNumericBinaryExp(leftField_exp.clone(), rightField_exp.clone());
		AIfExp if_exp = new AIfExp();
		if_exp.setTest(notEquals_exp);
		if_exp.setThen(gt_exp);

		if_exp.setElse(buildStructuralLessThan(left_exp.clone(), right_exp.clone(), tupleCounter + 1, recCounter - 1));

		return if_exp;
	}

	private AValueDefinition buildValueDef(PExp exp, String name)
	{
		AValueDefinition valDef = new AValueDefinition();
		valDef.setType(exp.getType().clone());
		valDef.setExpression(exp.clone());

		AIdentifierPattern pattern = new AIdentifierPattern();
		pattern.setName(new LexNameToken(null, name, null));

		valDef.setPattern(pattern);

		return valDef;
	}

	private AVariableExp wrapName(ILexNameToken name)
	{
		AVariableExp r = new AVariableExp();
		r.setName(name.clone());
		r.setOriginal(name.getFullName());
		return r;
	}
	
	private int getLex(AExplicitFunctionDefinition mdef)
	{
		AFunctionType ftype = (AFunctionType) mdef.getType();
		
		if (ftype.getResult() instanceof AProductType)
		{
			AProductType type = (AProductType)ftype.getResult();
			return type.getTypes().size();
		}
		else
		{
			return 0;
		}
	}
}
