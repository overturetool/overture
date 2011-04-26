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

package org.overturetool.vdmj.pog;

import java.util.List;

import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.statements.ErrorCase;

public class OperationPostConditionObligation extends ProofObligation
{
	public OperationPostConditionObligation(
		ExplicitOperationDefinition op, POContextStack ctxt)
	{
		super(op.location, POType.OP_POST_CONDITION, ctxt);
		value = ctxt.getObligation(getExp(op.precondition, op.postcondition, null));
	}

	public OperationPostConditionObligation(
		ImplicitOperationDefinition op, POContextStack ctxt)
	{
		super(op.location, POType.OP_POST_CONDITION, ctxt);
		value = ctxt.getObligation(getExp(op.precondition, op.postcondition, op.errors));
	}

	private String getExp(Expression preexp, Expression postexp, List<ErrorCase> errs)
	{
		if (errs == null)
		{
			return postexp.toString();
		}
		else
		{
			StringBuilder sb = new StringBuilder();

			if (preexp != null)
			{
				sb.append("(");
				sb.append(preexp);
				sb.append(" and ");
				sb.append(postexp);
				sb.append(")");
			}
			else
			{
				sb.append(postexp);
			}

			for (ErrorCase err: errs)
			{
				sb.append(" or (");
				sb.append(err.left);
				sb.append(" and ");
				sb.append(err.right);
				sb.append(")");
			}

			return sb.toString();
		}
	}
}
