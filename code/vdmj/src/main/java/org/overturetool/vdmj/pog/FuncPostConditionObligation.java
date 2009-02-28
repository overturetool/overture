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

import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.util.Utils;

public class FuncPostConditionObligation extends ProofObligation
{
	public FuncPostConditionObligation(
		ExplicitFunctionDefinition func, POContextStack ctxt)
	{
		super(func.location, POType.FUNC_POST_CONDITION, ctxt);

		value = ctxt.getObligation(generate(
			func.predef, func.postdef, func.paramPatternList, func.body.toString()));
	}

	public FuncPostConditionObligation(
		ImplicitFunctionDefinition func, POContextStack ctxt)
	{
		super(func.location, POType.FUNC_POST_CONDITION, ctxt);
		String body = null;

		if (func.body == null)
		{
			body = func.result.pattern.toString();
		}
		else
		{
			body = func.body.toString();
		}

		value = ctxt.getObligation(generate(
			func.predef, func.postdef,	func.getParamPatternList(), body));
	}

	private String generate(
		ExplicitFunctionDefinition predef,
		ExplicitFunctionDefinition postdef,
		List<PatternList> params, String body)
	{
		StringBuilder sb = new StringBuilder();

		if (predef != null)
		{
			sb.append(predef.name.name);
			sb.append(Utils.listToString("(", params, ", ", ")"));
			sb.append(" => ");
		}

		sb.append(postdef.name.name);
		sb.append(Utils.listToString("(", params, ", ", ""));
		sb.append(", ");
		sb.append(body);
		sb.append(")");

		return sb.toString();
	}
}
