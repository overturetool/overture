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

import org.overture.ast.expressions.ACompBinaryExp;
import org.overture.ast.types.assistants.PTypeAssistantTC;


public class FuncComposeObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8813166638915813635L;

	public FuncComposeObligation(
		ACompBinaryExp exp, String pref1, String pref2, POContextStack ctxt)
	{
		super(exp.getLocation(), POType.FUNC_COMPOSE, ctxt);
		StringBuilder sb = new StringBuilder();

		sb.append("forall arg:");
		sb.append(PTypeAssistantTC.getFunction(exp.getLeft().getType()).getParameters().get(0));
		sb.append(" & ");

		if (pref2 == null || !pref2.equals(""))
		{
    		if (pref2 != null)
    		{
        		sb.append(pref2);
        		sb.append("(arg) => ");
    		}
    		else
    		{
        		sb.append("pre_(");
        		sb.append(exp.getRight());
        		sb.append(", arg) => ");
    		}
		}

		if (pref1 != null)
		{
    		sb.append(pref1);
    		sb.append("(");
    		sb.append(exp.getRight());
    		sb.append("(arg))");
		}
		else
		{
    		sb.append("pre_(");
    		sb.append(exp.getLeft());
    		sb.append(", ");
    		sb.append(exp.getRight());
    		sb.append("(arg))");
		}

		value = ctxt.getObligation(sb.toString());
	}
}
