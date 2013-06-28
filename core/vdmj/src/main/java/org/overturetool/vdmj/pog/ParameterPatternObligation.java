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

import java.util.Iterator;
import java.util.List;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;

public class ParameterPatternObligation extends ProofObligation
{
	private final Definition predef;

	public ParameterPatternObligation(
		ExplicitFunctionDefinition def, POContextStack ctxt)
	{
		super(def.location, POType.FUNC_PATTERNS, ctxt);
		this.predef = def.predef;
		value = ctxt.getObligation(
			generate(def.paramPatternList, def.type.parameters, def.type.result));
	}

	public ParameterPatternObligation(
		ImplicitFunctionDefinition def, POContextStack ctxt)
	{
		super(def.location, POType.FUNC_PATTERNS, ctxt);
		this.predef = def.predef;
		value = ctxt.getObligation(
			generate(def.getParamPatternList(), def.type.parameters, def.type.result));
	}

	public ParameterPatternObligation(
		ExplicitOperationDefinition def, POContextStack ctxt)
	{
		super(def.location, POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.predef;
		value = ctxt.getObligation(
			generate(def.getParamPatternList(), def.type.parameters, def.type.result));
	}

	public ParameterPatternObligation(
		ImplicitOperationDefinition def, POContextStack ctxt)
	{
		super(def.location, POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.predef;
		value = ctxt.getObligation(
			generate(def.getListParamPatternList(), def.type.parameters, def.type.result));
	}

	private String generate(List<PatternList> plist, TypeList params, Type result)
	{
		StringBuilder foralls = new StringBuilder();
		StringBuilder argnames = new StringBuilder();
		StringBuilder ebindings = new StringBuilder();
		StringBuilder epredicates = new StringBuilder();

		String fprefix = "";
		String eprefix = "";
		int argn = 1;

		for (PatternList pl: plist)
		{
			Iterator<Type> titer = params.iterator();

			for (Pattern p: pl)
			{
				String aname = "arg" + argn++;
				Type atype = titer.next();

				foralls.append(fprefix);
				foralls.append(aname);
				foralls.append(":");
				foralls.append(atype);

				argnames.append(fprefix);
				argnames.append(aname);

				Expression pmatch = p.getMatchingExpression();
				ebindings.append(fprefix);
				ebindings.append(pmatch);
				ebindings.append(":");
				ebindings.append(atype);

				epredicates.append(eprefix);
				epredicates.append("(");
				epredicates.append(aname);
				epredicates.append(" = ");
				epredicates.append(pmatch);
				epredicates.append(")");

				fprefix = ", ";
				eprefix = " and ";
			}

			if (result instanceof FunctionType)
			{
				FunctionType ft = (FunctionType)result;
				result = ft.result;
				params = ft.parameters;
			}
			else
			{
				break;
			}
		}

		foralls.append(" &\n");
		String INDENT = "  ";

		if (predef != null)
		{
			foralls.append(INDENT);
			foralls.append(predef.name.name);
			foralls.append("(");
			foralls.append(argnames);
			foralls.append(")");
			foralls.append(" =>\n" + INDENT + INDENT);
			ebindings.append(" &\n" + INDENT + INDENT + INDENT);
		}
		else
		{
			foralls.append(INDENT);
			ebindings.append(" &\n" + INDENT + INDENT);
		}

		return "forall " + foralls.toString() +
			"exists " + ebindings.toString() + epredicates.toString();
	}
}
