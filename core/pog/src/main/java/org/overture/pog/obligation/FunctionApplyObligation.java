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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class FunctionApplyObligation extends ProofObligation
{
	private static final long serialVersionUID = -7146271970744572457L;

	public FunctionApplyObligation(PExp root, List<PExp> args,
			ILexNameToken prename, IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(root, POType.FUNC_APPLY, ctxt, root.getLocation(), af);

		/**
		 * If the root is an expression that evaluates to a function, we do not know which pre_f to call and prename is
		 * null. So we use the "pre_(root, args)" form. If the prename is defined, like "pre_f" then we can use
		 * "pre_f(args)". We should not attempt to create an obligation if there is no precondition - ie. we should not
		 * be here if prename is "".
		 */

		if (prename == null) // Root is an expression, so use pre_(root, args)
		{
			// pre_(root, args)
			APreExp preExp = new APreExp();
			preExp.setType(new ABooleanBasicType());
			preExp.setFunction(root.clone());
			preExp.setArgs(cloneListPExp(args));
			stitch = preExp.clone();
			valuetree.setPredicate(ctxt.getPredWithContext(preExp));
		} else
		{
			// pre_f(args)
			AVariableExp varExp = getVarExp(prename);
			varExp.setType(root.getType().clone());
			PExp pred = getApplyExp(varExp, cloneListPExp(args));
			pred.setType(new ABooleanBasicType());
			stitch = pred.clone();
			valuetree.setPredicate(ctxt.getPredWithContext(pred));
		}
	}

}
