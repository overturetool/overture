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
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.AErrorCase;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;
import org.overture.pog.utility.Substitution;

public class OperationPostCondition extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7717481924562707647L;
	private IPogAssistantFactory af;

	public OperationPostCondition(AExplicitOperationDefinition op, List<AInstanceVariableDefinition> state,
			IPOContextStack ctxt, IPogAssistantFactory af)
	{
		super(op, POType.OP_POST_CONDITION, ctxt, op.getLocation());
		this.af = af;
		PExp pred = buildExp(op.getPrecondition(), op.getPostcondition().clone(), null);
		pred = guardOldVars(pred, state);
		// valuetree.setContext(ctxt.getContextNodeList());
		valuetree.setPredicate(ctxt.getPredWithContext(pred));
	}

	public OperationPostCondition(AImplicitOperationDefinition op,
			IPOContextStack ctxt, IPogAssistantFactory af)
	{
		super(op, POType.OP_POST_CONDITION, ctxt, op.getLocation());
		this.af = af;
		valuetree.setPredicate(ctxt.getPredWithContext(buildExp(op.getPrecondition(), op.getPostcondition().clone(), op.clone().getErrors())));
	}

	private PExp guardOldVars(PExp pred, List<AInstanceVariableDefinition> state)
	{
		List<Substitution> subs = new LinkedList<Substitution>();
		for (AInstanceVariableDefinition i : state)
		{
			AVariableExp var_exp = new AVariableExp();
			ILexNameToken it = i.getName();
			String safename = it.getOldName().toString() + "$";
			ILexNameToken safeToken = new LexNameToken(it.getModule(), safename, it.getLocation().clone());
			var_exp.setName(safeToken);
			var_exp.setType(i.getType().clone());
			var_exp.setOriginal(i.getOldname().toString());
			subs.add(new Substitution(i.getOldname(), var_exp));
		}
		for (Substitution sub : subs)
		{
			try
			{
				pred = pred.apply(af.getVarSubVisitor(), sub);
			} catch (AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pred;
	}

	private PExp handlePrePost(PExp preexp, PExp postexp, List<AErrorCase> errs)
	{
		if (preexp != null)
		{
			// (preexp and postexp)
			AAndBooleanBinaryExp andExp = AstExpressionFactory.newAAndBooleanBinaryExp(preexp.clone(), postexp);

			return andExp;
		} else
		{
			return postexp;
		}
	}

	private PExp buildExp(PExp preexp, PExp postexp, List<AErrorCase> errs)
	{
		if (errs == null || errs.isEmpty())
		{
			return postexp;
		} else
		{// handled prepost or errors
			AOrBooleanBinaryExp orExp = new AOrBooleanBinaryExp();
			orExp.setLeft(handlePrePost(preexp.clone(), postexp, errs));
			PExp errorsExp = (buildErrsExp(errs));
			orExp.setRight(errorsExp);

			return orExp;
		}
	}

	private PExp handleErrorCase(AErrorCase err)
	{
		// (errlet and errright)
		AAndBooleanBinaryExp andExp = new AAndBooleanBinaryExp();
		andExp.setLeft(err.getLeft());
		andExp.setRight(err.getRight());
		return andExp;
	}

	private PExp buildErrsExp(List<AErrorCase> errs)
	{
		if (errs.size() == 1)
		{ // termination case
			return handleErrorCase(errs.get(0));
		} else
		{ // recurse on error list
			AOrBooleanBinaryExp orExp = new AOrBooleanBinaryExp();
			orExp.setLeft(handleErrorCase(errs.get(0)));
			orExp.setRight(buildErrsExp(errs.subList(1, errs.size() - 1)));
			return orExp;
		}
	}

}
