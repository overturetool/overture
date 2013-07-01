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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.typechecker.NameScope;
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
		StringBuilder exists = new StringBuilder();

		String INDENT = "  ";
		String fprefix = "";
		String lprefix = "";
		int argn = 1;

		for (PatternList pl: plist)
		{
			StringBuilder ebindings = new StringBuilder();
			StringBuilder epredicates = new StringBuilder();
			Iterator<Type> titer = params.iterator();
			String eprefix = "";
			String aprefix = "";
			int bindn = 1;
			
			if (!pl.isEmpty())
			{
				argnames.append("(");
				
				if (predef != null)
				{
					exists.append(INDENT);
					exists.append(INDENT);
					exists.append(lprefix);
					exists.append("(exists ");
				}
				else
				{
					exists.append(INDENT);
					exists.append(lprefix);
					exists.append("(exists ");
				}
				
				Set<String> existingBindings = new HashSet<String>();
	
				for (Pattern p: pl)
				{
					String aname = "arg" + argn++;
					String bname = "bind" + bindn++;
					Type atype = titer.next();
					Expression pmatch = p.getMatchingExpression();
					DefinitionList dlist = p.getDefinitions(atype, NameScope.LOCAL);
					
					foralls.append(fprefix);
					foralls.append(aname);
					foralls.append(":");
					foralls.append(atype);
	
					argnames.append(aprefix);
					argnames.append(aname);
	
					ebindings.append(aprefix);
					aprefix = ", ";
					ebindings.append(bname);
					ebindings.append(":");
					ebindings.append(atype);
	
					for (Definition def: dlist)
					{
						if (def.name != null && !existingBindings.contains(def.name.name))
						{
							ebindings.append(aprefix);
							ebindings.append(def.name.name);
							ebindings.append(":");
							ebindings.append(def.getType());
							existingBindings.add(def.name.name);
						}
					}
	
					epredicates.append(eprefix);
					eprefix = " and ";
					epredicates.append("(");
					epredicates.append(aname);
					epredicates.append(" = ");
					epredicates.append(bname);
					epredicates.append(") and (");
					epredicates.append(pmatch);
					epredicates.append(" = ");
					epredicates.append(bname);
					epredicates.append(")");
	
					fprefix = ", ";
				}
				
				argnames.append(")");
				exists.append(ebindings.toString());
				exists.append(" & ");
				exists.append(epredicates.toString());
				exists.append(")\n");
				lprefix = "and ";
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

		if (predef != null)
		{
			foralls.append(INDENT);
			foralls.append(predef.name.name);
			foralls.append(argnames);
			foralls.append(" =>\n");
		}

		return "forall " + foralls.toString() + exists.toString();
	}
}
