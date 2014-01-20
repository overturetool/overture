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

package org.overture.pog.obligation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.pog.assistant.IPogAssistantFactory;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class ParameterPatternObligation extends ProofObligation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6831031423902894299L;
	private final PDefinition predef;

	public ParameterPatternObligation(AExplicitFunctionDefinition def,
			POContextStack ctxt)
	{
		super(def.getLocation(), POType.FUNC_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(generate(def.getParamPatternList(), ((AFunctionType) def.getType()).getParameters(), ((AFunctionType) def.getType()).getResult(),ctxt.assistantFactory));
	}

	public ParameterPatternObligation(AImplicitFunctionDefinition def,
			POContextStack ctxt)
	{
		super(def.getLocation(), POType.FUNC_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(generate(AImplicitFunctionDefinitionAssistantTC.getParamPatternList(def), ((AFunctionType) def.getType()).getParameters(), ((AFunctionType) def.getType()).getResult(),ctxt.assistantFactory));
	}

	public ParameterPatternObligation(AExplicitOperationDefinition def,
			POContextStack ctxt)
	{
		super(def.getLocation(), POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(generate(AExplicitOperationDefinitionAssistantTC.getParamPatternList(def), ((AOperationType) def.getType()).getParameters(), ((AOperationType) def.getType()).getResult(), ctxt.assistantFactory));
	}

	public ParameterPatternObligation(AImplicitOperationDefinition def,
			POContextStack ctxt)
	{
		super(def.getLocation(), POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(generate(AImplicitOperationDefinitionAssistantTC.getListParamPatternList(def), ((AOperationType) def.getType()).getParameters(), ((AOperationType) def.getType()).getResult(),ctxt.assistantFactory));
	}

	private String generate(List<List<PPattern>> plist, List<PType> params,
			PType result, IPogAssistantFactory assistantFactory)
	{
		StringBuilder foralls = new StringBuilder();
		StringBuilder argnames = new StringBuilder();
		StringBuilder exists = new StringBuilder();
		

		String INDENT = "  ";
		String fprefix = "";
		String lprefix = "";
		int argn = 1;

		for (List<PPattern> pl : plist)
		{
			StringBuilder ebindings = new StringBuilder();
			StringBuilder epredicates = new StringBuilder();
			Iterator<PType> titer = params.iterator();
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
	
				for (PPattern p: pl)
				{
					String aname = "arg" + argn++;
					String bname = "bind" + bindn++;
					PType atype = titer.next();
					PExp pmatch = PPatternAssistantTC.getMatchingExpression(p);
					List<PDefinition> dlist = PPatternAssistantTC.getDefinitions(p, atype, NameScope.LOCAL);
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
	
					for (PDefinition def: dlist)
					{
						if (def.getName() != null && !existingBindings.contains(def.getName().getName()))
						{
							ebindings.append(aprefix);
							ebindings.append(def.getName().getName());
							ebindings.append(":");
							ebindings.append(def.getType());
							existingBindings.add(def.getName().getName());
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

			if (result instanceof AFunctionType)
			{
				AFunctionType ft = (AFunctionType) result;
				result = ft.getResult();
				params = ft.getParameters();
			} else
			{
				break;
			}
		}

		foralls.append(" &\n");

		if (predef != null)
		{
			foralls.append(INDENT);
			foralls.append(predef.getName().getName());
			foralls.append(argnames);
			foralls.append(" =>\n");
		}

		return "forall " + foralls.toString() + exists.toString();
	}
}
