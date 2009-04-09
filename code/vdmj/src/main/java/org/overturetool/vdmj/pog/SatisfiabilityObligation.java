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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.types.PatternListTypePair;

public class SatisfiabilityObligation extends ProofObligation
{
	public SatisfiabilityObligation(ImplicitFunctionDefinition func,
		POContextStack ctxt)
	{
		super(func.location, POType.FUNC_SATISFIABILITY, ctxt);
		StringBuilder sb = new StringBuilder();

		if (func.predef != null)
		{
    		sb.append(func.predef.name.name);
    		sb.append("(");
    		appendParamPatterns(sb, func.parameterPatterns);
    		sb.append(")");
    		sb.append(" => ");
		}

		sb.append("exists ");
		sb.append(func.result);
		sb.append(" & ");
		sb.append(func.postdef.name.name);
		sb.append("(");
		appendParamPatterns(sb, func.parameterPatterns);
		sb.append(", ");
		sb.append(func.result.pattern);
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}

	public SatisfiabilityObligation(ImplicitOperationDefinition op,
		Definition stateDefinition, POContextStack ctxt)
	{
		super(op.location, POType.OP_SATISFIABILITY, ctxt);
		StringBuilder sb = new StringBuilder();

		if (op.predef != null)
		{
    		sb.append(op.predef.name.name);
    		sb.append("(");
    		appendParamPatterns(sb, op.parameterPatterns);
    		appendStatePatterns(sb, stateDefinition, true, false);
    		sb.append(")");
    		sb.append(" =>\n");
		}

		sb.append("exists ");
		sb.append(op.result);
		appendStatePatterns(sb, stateDefinition, false, true);
		sb.append(" & ");
		sb.append(op.postdef.name.name);
		sb.append("(");
		appendParamPatterns(sb, op.parameterPatterns);
		sb.append(", ");
		sb.append(op.result.pattern);
		appendStatePatterns(sb, stateDefinition, true, false);
		appendStatePatterns(sb, stateDefinition, false, false);
		sb.append(")");

		value = ctxt.getObligation(sb.toString());
	}

	private void appendStatePatterns(
		StringBuilder sb, Definition state, boolean old, boolean typed)
	{
		if (state == null)
		{
			return;
		}
		else if (state instanceof StateDefinition)
		{
			if (old)
			{
				sb.append(", oldstate");
			}
			else
			{
				sb.append(", newstate");
			}

			if (typed)
			{
				StateDefinition def = (StateDefinition)state;
				sb.append(":");
				sb.append(def.name.name);
			}
		}
		else
		{
			if (old)
			{
				sb.append(", oldself");
			}
			else
			{
				sb.append(", newself");
			}

			if (typed)
			{
				ClassDefinition def = (ClassDefinition)state;
				sb.append(":");
				sb.append(def.name.name);
			}
		}
	}

	private void appendParamPatterns(
		StringBuilder sb, List<PatternListTypePair> params)
	{
		String sep = "";

		for (PatternListTypePair pltp: params)
		{
			sb.append(sep);
			sb.append(pltp.patterns.getMatchingExpression());
			sep = ", ";
		}
	}
}
