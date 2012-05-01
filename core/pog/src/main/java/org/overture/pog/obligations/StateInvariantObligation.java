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

import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistantTC;
import org.overture.ast.statements.AAssignmentStm;

public class StateInvariantObligation extends ProofObligation
{
	public StateInvariantObligation(AAssignmentStm ass, POContextStack ctxt)
	{
		super(ass.getLocation(), POType.STATE_INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();
		sb.append("-- After ");
		sb.append(ass);
		sb.append("\n");

		if (ass.getClassDefinition() != null)
		{
			sb.append(invDefs(ass.getClassDefinition()));
		}
		else	// must be because we have a module state invariant
		{
			AStateDefinition def = ass.getStateDefinition();

			sb.append("let ");
			sb.append(def.getInvPattern());
			sb.append(" = ");
			sb.append(def.getName());
			sb.append(" in ");
			sb.append(def.getInvExpression());
		}

		value = ctxt.getObligation(sb.toString());
	}

	public StateInvariantObligation(
		AClassInvariantDefinition def,
		POContextStack ctxt)
	{
		super(def.getLocation(), POType.STATE_INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();
		sb.append("-- After instance variable initializers\n");
		sb.append(invDefs(def.getClassDefinition()));

    	value = ctxt.getObligation(sb.toString());
	}

	public StateInvariantObligation(
		AExplicitOperationDefinition def,
		POContextStack ctxt)
	{
		super(def.getLocation(), POType.STATE_INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();
		sb.append("-- After ");
		sb.append(def.getName());
		sb.append(" constructor body\n");
		sb.append(invDefs(def.getClassDefinition()));

    	value = ctxt.getObligation(sb.toString());
	}

	public StateInvariantObligation(
		AImplicitOperationDefinition def,
		POContextStack ctxt)
	{
		super(def.getLocation(), POType.STATE_INVARIANT, ctxt);
		StringBuilder sb = new StringBuilder();
		sb.append("-- After ");
		sb.append(def.getName());
		sb.append(" constructor body\n");
		sb.append(invDefs(def.getClassDefinition()));

    	value = ctxt.getObligation(sb.toString());
	}

	private String invDefs(SClassDefinition def)
	{
		StringBuilder sb = new StringBuilder();
		List<PDefinition> invdefs = SClassDefinitionAssistantTC.getInvDefs(def);
		String sep = "";

		for (PDefinition d: invdefs)
		{
			AClassInvariantDefinition cid = (AClassInvariantDefinition)d;
			sb.append(sep);
			sb.append(cid.getExpression());
			sep = " and ";
		}

    	return sb.toString();
	}
}
