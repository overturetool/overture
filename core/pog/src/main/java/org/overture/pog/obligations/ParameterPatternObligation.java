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

import java.util.Iterator;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;

public class ParameterPatternObligation extends ProofObligation
{
	private final PDefinition predef;

	public ParameterPatternObligation(
		AExplicitFunctionDefinition def, POContextStack ctxt)
	{
		super(def.getLocation(), POType.FUNC_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(
			generate(def.getParamPatternList(), def.getType().getParameters(), def.getType().getResult()));
	}

	public ParameterPatternObligation(
		AImplicitFunctionDefinition def, POContextStack ctxt)
	{
		super(def.getLocation(), POType.FUNC_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(
			generate(def.getParamPatternList(), def.getType().getParameters(), def.getType().getResult()));
	}

	public ParameterPatternObligation(
		AExplicitOperationDefinition def, POContextStack ctxt)
	{
		super(def.getLocation(), POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(
			generate(def.getParamPatternList(), def.getType().getParameters(), def.getType().getResult()));
	}

	public ParameterPatternObligation(
		AImplicitOperationDefinition def, POContextStack ctxt)
	{
		super(def.getLocation(), POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(
			generate(def.getListParamPatternList(), def.getType().getParameters(), def.getType().getResult()));
	}

	private String generate(List<List<PPattern>> plist, List<PType> params, PType result)
	{
		StringBuilder foralls = new StringBuilder();
		StringBuilder argnames = new StringBuilder();
		StringBuilder exists = new StringBuilder();

		foralls.append("forall ");
		String fprefix = "";
		String eprefix = "";
		int argn = 1;

		for (List<PPattern> pl: plist)
		{
			Iterator<PType> titer = params.iterator();

			for (PPattern p: pl)
			{
				String aname = "arg" + argn++;
				PType atype = titer.next();

				if (!(p instanceof AIgnorePattern) &&
					!(p instanceof AIdentifierPattern))
				{
					foralls.append(fprefix);
					foralls.append(aname);
					foralls.append(":");
					foralls.append(atype);

					argnames.append(fprefix);
					argnames.append(aname);

					Expression pmatch = p.getMatchingExpression();
					exists.append(eprefix);
					exists.append("(exists ");
					exists.append(pmatch);
					exists.append(":");
					exists.append(atype);
					exists.append(" & ");
					exists.append(aname);
					exists.append(" = ");
					exists.append(pmatch);
					exists.append(")");

					fprefix = ", ";
					eprefix = " and\n  ";

					if (predef != null)
					{
						eprefix = eprefix + "  ";
					}
				}
			}

			if (result instanceof AFunctionType)
			{
				AFunctionType ft = (AFunctionType)result;
				result = ft.getResult();
				params = ft.getParameters();
			}
			else
			{
				break;
			}
		}

		foralls.append(" &\n");

		if (predef != null)
		{
			foralls.append("  ");
			foralls.append(predef.getName().name);
			foralls.append("(");
			foralls.append(argnames);
			foralls.append(")");
			foralls.append(" =>\n    ");
		}
		else
		{
			foralls.append("  ");
		}

		return foralls.toString() + exists.toString();
	}
}
