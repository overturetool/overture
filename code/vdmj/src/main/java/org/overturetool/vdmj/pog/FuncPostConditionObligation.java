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

import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.expressions.NotYetSpecifiedExpression;
import org.overturetool.vdmj.expressions.SubclassResponsibilityExpression;
import org.overturetool.vdmj.patterns.PatternList;

public class FuncPostConditionObligation extends ProofObligation
{
	public FuncPostConditionObligation(
		ExplicitFunctionDefinition func, POContextStack ctxt)
	{
		super(func.location, POType.FUNC_POST_CONDITION, ctxt);

		StringBuilder params = new StringBuilder();

		for (PatternList pl: func.paramPatternList)
		{
			params.append(pl.getMatchingValues());
		}

		String body = null;

		if (func.body instanceof NotYetSpecifiedExpression ||
			func.body instanceof SubclassResponsibilityExpression)
		{
			// We have to say "f(a)" because we have no expression yet

			StringBuilder sb = new StringBuilder();
			sb.append(func.name.name);
			sb.append("(");
			sb.append(params);
			sb.append(")");
			body = sb.toString();
		}
		else
		{
			body = func.body.toString();
		}

		value = ctxt.getObligation(generate(func.predef, func.postdef, params, body));
	}

	public FuncPostConditionObligation(
		ImplicitFunctionDefinition func, POContextStack ctxt)
	{
		super(func.location, POType.FUNC_POST_CONDITION, ctxt);

		StringBuilder params = new StringBuilder();

		for (PatternList pl: func.getParamPatternList())
		{
			params.append(pl.getMatchingValues());
		}

		String body = null;

		if (func.body == null)
		{
			body = func.result.pattern.toString();
		}
		else if (func.body instanceof NotYetSpecifiedExpression ||
				 func.body instanceof SubclassResponsibilityExpression)
		{
			// We have to say "f(a)" because we have no expression yet

			StringBuilder sb = new StringBuilder();
			sb.append(func.name.name);
			sb.append("(");
			sb.append(params);
			sb.append(")");
			body = sb.toString();
		}
		else
		{
			body = func.body.toString();
		}

		value = ctxt.getObligation(generate(func.predef, func.postdef, params, body));
	}

	private String generate(
		ExplicitFunctionDefinition predef,
		ExplicitFunctionDefinition postdef,
		StringBuilder params, String body)
	{
		StringBuilder sb = new StringBuilder();

		if (predef != null)
		{
			sb.append(predef.name.name);
			sb.append("(");
			sb.append(params);
			sb.append(") => ");
		}

		sb.append(postdef.name.name);
		sb.append("(");
		sb.append(params);
		sb.append(", ");
		sb.append(body);
		sb.append(")");

		return sb.toString();
	}
}
