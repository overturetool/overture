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

package org.overture.pog.obligations;

import java.util.List;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AErrorCase;

public class OperationPostConditionObligation extends ProofObligation
{
	public OperationPostConditionObligation(
		AExplicitOperationDefinition op, POContextStack ctxt)
	{
		super(op.getLocation(), POType.OP_POST_CONDITION, ctxt);
		value = ctxt.getObligation(getExp(op.getPrecondition(), op.getPostcondition(), null));
	}

	public OperationPostConditionObligation(
		AImplicitOperationDefinition op, POContextStack ctxt)
	{
		super(op.getLocation(), POType.OP_POST_CONDITION, ctxt);
		value = ctxt.getObligation(getExp(op.getPrecondition(), op.getPostcondition(), op.getErrors()));
	}

	private String getExp(PExp preexp, PExp postexp, List<AErrorCase> errs)
	{
		if (errs.isEmpty())
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

			for (AErrorCase err: errs)
			{
				sb.append(" or (");
				sb.append(err.getLeft());
				sb.append(" and ");
				sb.append(err.getRight());
				sb.append(")");
			}

			return sb.toString();
		}
	}
}
