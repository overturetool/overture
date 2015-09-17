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
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class FuncIterationObligation extends ProofObligation
{
	/**
	 * VDM Bits: f: nat -> nat1 f(a) == a + 1 pre a < 10; g: nat -> nat g(n) == (f ** n)(0); Generate PO: (forall n:nat
	 * & n > 1 => forall arg:nat & pre_f(arg) => pre_f(f(arg)))
	 */

	private static final long serialVersionUID = -6041213040266345023L;

	public FuncIterationObligation(AStarStarBinaryExp exp,
			ILexNameToken preName, IPOContextStack ctxt,
			IPogAssistantFactory assistantFactory) throws AnalysisException
	{
		super(exp, POType.FUNC_ITERATION, ctxt, exp.getLocation(), assistantFactory);

		// n > 1
		AGreaterNumericBinaryExp gTExp = AstExpressionFactory.newAGreaterNumericBinaryExp(exp.getRight().clone(), getIntLiteral(1));

		// forall n :T & P(X)
		AForAllExp forAllExp = new AForAllExp();
		ILexNameToken arg = getUnique("arg");
		List<PMultipleBind> bindList = getMultipleTypeBindList(assistantFactory.createPTypeAssistant().getNumeric(exp.getRight().getType().clone()), arg);
		forAllExp.setBindList(bindList);
		forAllExp.setPredicate(getPredicate(exp.clone(), preName.clone(), arg));

		// n > 1 => forall n :T & P(X)
		AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(gTExp, forAllExp);
		
		stitch = impliesExp.clone();
		valuetree.setPredicate(ctxt.getPredWithContext(impliesExp));
	}

	PExp getImplies(PExp preExp, PExp leftExp, ILexNameToken preName,
			ILexNameToken arg)
	{
		// pre_f(a)
		AApplyExp pre_exp = getApplyExp(preExp, getVarExp(arg));

		// f(a)
		AApplyExp left_exp = getApplyExp(leftExp, getVarExp(arg));

		// pre_f(f(a))
		AApplyExp preleft_exp = getApplyExp(pre_exp, left_exp);

		// pre_f(a) => pre_f(f(a))
		AImpliesBooleanBinaryExp impliesExp = AstExpressionFactory.newAImpliesBooleanBinaryExp(pre_exp, preleft_exp);
		
		return impliesExp;

	}

	private PExp getPredicate(AStarStarBinaryExp exp, ILexNameToken preName,
			ILexNameToken arg)
	{
		if (preName != null)
		{

			return getImplies(getVarExp(preName), exp.getLeft(), preName, arg);

		} else
		{ // if no existing pre_f, build it
			AApplyExp applyExp = new AApplyExp();

			ILexNameToken prename = new LexNameToken("", "pre_", null);
			applyExp.setRoot(getVarExp(prename));
			List<PExp> argList = new LinkedList<PExp>();
			argList.add(exp.getLeft());
			applyExp.setArgs(argList);

			return getImplies(applyExp, exp.getLeft(), prename, arg);

		}
	}

}
