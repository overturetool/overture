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

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class FuncComposeObligation extends ProofObligation
{
	private static final long serialVersionUID = 8813166638915813635L;

	// add last parameter to pass assistantFactory to the method.
	public FuncComposeObligation(ACompBinaryExp exp, ILexNameToken pref1,
			ILexNameToken pref2, IPOContextStack ctxt,
			IPogAssistantFactory assistantFactory) throws AnalysisException
	{
		super(exp, POType.FUNC_COMPOSE, ctxt, exp.getLocation(), assistantFactory);

		// Function composition must be of two functions with a single parameter each.
		// The obligation depends on whether the left/right expressions of the "comp"
		// are not directly functions (pref is null) or whether they are functions
		// that do or do not have preconditions (pref is "" or a name like "pre_FN").
		//
		// Given f1(a:A), f2(b:B), the obligation for "f1 comp f2" is then:
		//
		// forall arg:A & pre_f2(arg) => pre_f1(f2(arg))
		//
		// And for exp1 comp exp2 (where the arguments evaluate to functions):
		//
		// forall arg:A & pre_(exp2, arg) => pre_(exp1, exp2(arg))
		//
		// Similarly for mixtures of functions and expressions. If f2 is a function without
		// a precondition, there is not LHS to the =>, only the RHS.

		ILexNameToken arg = getUnique("arg");

		AForAllExp forallExp = new AForAllExp();
		PType leftPType = assistantFactory.createPTypeAssistant().getFunction(exp.getLeft().getType()).getParameters().get(0).clone();
		forallExp.setBindList(getMultipleTypeBindList(leftPType, arg));
		PExp firstPart = null;

		if (pref2 == null || pref2.getFullName() != "") // An expression or a function with a precondition
		{
			if (pref2 != null)
			{
				// pref2(arg) =>
				firstPart = getApplyExp(getVarExp(pref2), getVarExp(arg));
			} else
			{
				// pre_(exp.getRight(), arg) =>
				APreExp preExp = new APreExp();
				preExp.setFunction(exp.getRight().clone());
				List<PExp> args = new ArrayList<PExp>();
				args.add(getVarExp(arg));
				preExp.setArgs(args);

				firstPart = preExp;
			}
		}

		PExp secondPart = null;

		if (pref1 != null)
		{
			// pref1(exp.getRight()(arg))
			secondPart = getApplyExp(getVarExp(pref1), getApplyExp(exp.getRight(), getVarExp(arg)));
		} else
		{
			// pre_(exp.getLeft(), exp.getRight()(arg))
			APreExp preExp = new APreExp();
			preExp.setFunction(exp.getLeft().clone());
			List<PExp> args = new ArrayList<PExp>();
			args.add(getApplyExp(exp.getRight().clone(), getVarExp(arg)));
			preExp.setArgs(args);
			secondPart = preExp;
		}

		if (firstPart == null)
		{
			forallExp.setPredicate(secondPart);
		} else
		{
			forallExp.setPredicate(AstExpressionFactory.newAImpliesBooleanBinaryExp(firstPart, secondPart));
		}

		stitch = forallExp.clone();
		valuetree.setPredicate(ctxt.getPredWithContext(forallExp));
	}
}
