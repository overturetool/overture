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

import java.util.Iterator;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
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
		value = ctxt.getObligation(generate(def.getParamPatternList(), ((AFunctionType) def.getType()).getParameters(), ((AFunctionType) def.getType()).getResult()));
	}

	public ParameterPatternObligation(AImplicitFunctionDefinition def,
			POContextStack ctxt)
	{
		super(def.getLocation(), POType.FUNC_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(generate(AImplicitFunctionDefinitionAssistantTC.getParamPatternList(def), ((AFunctionType) def.getType()).getParameters(), ((AFunctionType) def.getType()).getResult()));
	}

	public ParameterPatternObligation(AExplicitOperationDefinition def,
			POContextStack ctxt)
	{
		super(def.getLocation(), POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(generate(AExplicitOperationDefinitionAssistantTC.getParamPatternList(def), ((AOperationType) def.getType()).getParameters(), ((AOperationType) def.getType()).getResult()));
	}

	public ParameterPatternObligation(AImplicitOperationDefinition def,
			POContextStack ctxt)
	{
		super(def.getLocation(), POType.OPERATION_PATTERNS, ctxt);
		this.predef = def.getPredef();
		value = ctxt.getObligation(generate(AImplicitOperationDefinitionAssistantTC.getListParamPatternList(def), ((AOperationType) def.getType()).getParameters(), ((AOperationType) def.getType()).getResult()));
	}

	private String generate(List<List<PPattern>> plist, List<PType> params,
			PType result)
	{
		StringBuilder foralls = new StringBuilder();
		StringBuilder argnames = new StringBuilder();
		StringBuilder ebindings = new StringBuilder();
		StringBuilder epredicates = new StringBuilder();

		String fprefix = "";
		String eprefix = "";
		int argn = 1;

		for (List<PPattern> pl : plist)
		{
			Iterator<PType> titer = params.iterator();

			for (PPattern p : pl)
			{
				String aname = "arg" + argn++;
				PType atype = titer.next();

				foralls.append(fprefix);
				foralls.append(aname);
				foralls.append(":");
				foralls.append(atype);

				argnames.append(fprefix);
				argnames.append(aname);

				PExp pmatch = PPatternAssistantTC.getMatchingExpression(p);
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
		String INDENT = "  ";
		
		if (predef != null)
		{
			foralls.append(INDENT);
			foralls.append(predef.getName().getName());
			foralls.append("(");
			foralls.append(argnames);
			foralls.append(")");
			foralls.append(" =>\n" + INDENT + INDENT);
			ebindings.append(" &\n" + INDENT + INDENT + INDENT);
		} else
		{
			foralls.append(INDENT);
			ebindings.append(" &\n" + INDENT + INDENT);
		}

		return "forall " + foralls.toString() +
				"exists " + ebindings.toString() + epredicates.toString();
	}
}
