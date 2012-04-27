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

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.assistants.AImplicitFunctionDefinitionAssistant;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
import org.overturetool.vdmj.util.Utils;

public class FuncPostConditionObligation extends ProofObligation
{
	public FuncPostConditionObligation(AExplicitFunctionDefinition func,
			POContextStack ctxt)
	{
		super(func.getLocation(), POType.FUNC_POST_CONDITION, ctxt);

		StringBuilder params = new StringBuilder();

		for (List<PPattern> pl : func.getParamPatternList())
		{
			String postfix = "";
			for (PExp p : PPatternAssistantTC.getMatchingExpressionList(pl))
			{
				params.append(postfix);
				params.append(p);
				postfix = ", ";
			}
		}

		String body = null;

		if (func.getBody() instanceof ANotYetSpecifiedExp
				|| func.getBody() instanceof ASubclassResponsibilityExp)
		{
			// We have to say "f(a)" because we have no expression yet

			StringBuilder sb = new StringBuilder();
			sb.append(func.getName().name);
			sb.append("(");
			sb.append(params);
			sb.append(")");
			body = sb.toString();
		} else
		{
			body = func.getBody().toString();
		}

		value = ctxt.getObligation(generate(func.getPredef(), func.getPostdef(), params, body));
	}

	public FuncPostConditionObligation(AImplicitFunctionDefinition func,
			POContextStack ctxt)
	{
		super(func.getLocation(), POType.FUNC_POST_CONDITION, ctxt);

		StringBuilder params = new StringBuilder();

		for (List<PPattern> pl : AImplicitFunctionDefinitionAssistant.getParamPatternList(func))
		{
			params.append(Utils.listToString(PPatternAssistantTC.getMatchingExpressionList(pl)));
		}

		String body = null;

		if (func.getBody() == null)
		{
			body = func.getResult().getPattern().toString();
		} else if (func.getBody() instanceof ANotYetSpecifiedExp
				|| func.getBody() instanceof ASubclassResponsibilityExp)
		{
			// We have to say "f(a)" because we have no expression yet

			StringBuilder sb = new StringBuilder();
			sb.append(func.getName().name);
			sb.append("(");
			sb.append(params);
			sb.append(")");
			body = sb.toString();
		} else
		{
			body = func.getBody().toString();
		}

		value = ctxt.getObligation(generate(func.getPredef(), func.getPostdef(), params, body));
	}

	private String generate(AExplicitFunctionDefinition predef,
			AExplicitFunctionDefinition postdef, StringBuilder params,
			String body)
	{
		StringBuilder sb = new StringBuilder();

		if (predef != null)
		{
			sb.append(predef.getName().name);
			sb.append("(");
			sb.append(params);
			sb.append(") => ");
		}

		sb.append(postdef.getName().name);
		sb.append("(");
		sb.append(params);
		sb.append(", ");
		sb.append(body);
		sb.append(")");

		return sb.toString();
	}
}
