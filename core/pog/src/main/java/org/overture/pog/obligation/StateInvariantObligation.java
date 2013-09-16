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

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.POType;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

public class StateInvariantObligation extends ProofObligation
{
	private static final long serialVersionUID = -5828298910806421399L;

	public StateInvariantObligation(AAssignmentStm ass, IPOContextStack ctxt)
	{
		super(ass, POType.STATE_INVARIANT, ctxt);
		
		if (ass.getClassDefinition() != null)
		{
			valuetree.setPredicate(ctxt.getPredWithContext(invDefs(ass.getClassDefinition())));
		}
		else
		{
			AStateDefinition def = ass.getStateDefinition();
			ALetDefExp letExp = new ALetDefExp();
			
			List<PDefinition> invDefs = new Vector<PDefinition>();
			AEqualsDefinition local = new AEqualsDefinition();
			local.setPattern(def.getInvPattern());
			local.setName(def.getName());
			invDefs.add(local);
			letExp.setLocalDefs(invDefs);
			letExp.setExpression(def.getInvExpression());

			valuetree.setPredicate(ctxt.getPredWithContext(letExp));
		}

//		valuetree.setContext(ctxt.getContextNodeList());
	}

	public StateInvariantObligation(AClassInvariantDefinition def, IPOContextStack ctxt)
	{
		super(def, POType.STATE_INVARIANT, ctxt);
		// After instance variable initializers
		valuetree.setPredicate(ctxt.getPredWithContext(invDefs(def.getClassDefinition())));
//    	valuetree.setContext(ctxt.getContextNodeList());
	}

	public StateInvariantObligation(AExplicitOperationDefinition def, IPOContextStack ctxt)
	{
		super(def, POType.STATE_INVARIANT, ctxt);
		// After def.getName() constructor body
		valuetree.setPredicate(ctxt.getPredWithContext(invDefs(def.getClassDefinition())));
//    	valuetree.setContext(ctxt.getContextNodeList());
	}

	public StateInvariantObligation(AImplicitOperationDefinition def, IPOContextStack ctxt)
	{
		super(def, POType.STATE_INVARIANT, ctxt);
		// After def.getName() constructor body
		valuetree.setPredicate(ctxt.getPredWithContext(invDefs(def.getClassDefinition())));
//    	valuetree.setContext(ctxt.getContextNodeList());
	}

	private PExp invDefs(SClassDefinition def)
	{
		PExp root = null;
		
		for (PDefinition d: SClassDefinitionAssistantTC.getInvDefs(def))
		{
			AClassInvariantDefinition cid = (AClassInvariantDefinition)d;
			root = makeAnd(root, cid.getExpression());
		}

    	return root;
	}
}
