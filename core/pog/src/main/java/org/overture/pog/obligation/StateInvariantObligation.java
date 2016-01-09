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

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFieldField;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;
import org.overture.pog.utility.Substitution;
import org.overture.pog.visitors.IVariableSubVisitor;

public class StateInvariantObligation extends ProofObligation
{
	private static final long serialVersionUID = -5828298910806421399L;

	public final IPogAssistantFactory assistantFactory;

	public StateInvariantObligation(AAssignmentStm ass, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(ass, POType.STATE_INV, ctxt, ass.getLocation(), af);
		assistantFactory = af;

		if (ass.getClassDefinition() != null)
		{
			PExp old_invs = invDefs(ass.getClassDefinition());

			String hash;
			hash = ass.getTarget().apply(af.getStateDesignatorNameGetter());
			Substitution sub = new Substitution(new LexNameToken("", hash, null), ass.getExp().clone());
			PExp new_invs = old_invs.clone().apply(af.getVarSubVisitor(), sub);

			stitch = AstExpressionFactory.newAImpliesBooleanBinaryExp(old_invs, new_invs);
			valuetree.setPredicate(ctxt.getPredWithContext(stitch));
		} else
		{
			AStateDefinition def = ass.getStateDefinition();
			ALetDefExp letExp = new ALetDefExp();
			letExp.setType(def.getInvExpression().getType().clone());
			List<PDefinition> invDefs = new Vector<PDefinition>();
			AEqualsDefinition local = new AEqualsDefinition();
			local.setPattern(def.getInvPattern().clone());
			local.setName(def.getName().clone());
			AVariableExp varExp = getVarExp(def.getName());
			varExp.setType(def.getRecordType().clone());
			local.setTest(varExp);
			invDefs.add(local);
			letExp.setLocalDefs(invDefs);
			letExp.setExpression(def.getInvExpression().clone());

			stitch = letExp;
			valuetree.setPredicate(ctxt.getPredWithContext(stitch));
		}
	}

	public StateInvariantObligation(AClassInvariantDefinition def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.STATE_INV_INIT, ctxt, def.getLocation(), af);
		assistantFactory = af;

		// After instance variable initializers

		stitch = invDefs(def.getClassDefinition());
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}

	public StateInvariantObligation(AExplicitOperationDefinition def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.STATE_INV, ctxt, def.getLocation(), af);
		assistantFactory = af;

		// After def.getName() constructor body
		stitch = invDefs(def.getClassDefinition());
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}

	public StateInvariantObligation(AAtomicStm atom, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(atom, POType.STATE_INV, ctxt, atom.getLocation(), af);
		assistantFactory = af;

		PExp invApplyExp = makeInvApplyExp(atom);

		PExp invApplyExpForSub = invApplyExp.clone();

		List<Substitution> subs = new LinkedList<Substitution>();
		for (AAssignmentStm asgn : atom.getAssignments())
		{
			String hash = asgn.getTarget().apply(af.getStateDesignatorNameGetter());
			subs.add(new Substitution(hash, asgn.getExp().clone()));
		}
		IVariableSubVisitor varSubVisitor = af.getVarSubVisitor();
		for (Substitution sub : subs)
		{
			invApplyExpForSub = invApplyExpForSub.apply(varSubVisitor, sub);
		}

		stitch = AstExpressionFactory.newAImpliesBooleanBinaryExp(invApplyExp, invApplyExpForSub);
		valuetree.setPredicate(stitch);
	}

	private PExp makeInvApplyExp(AAtomicStm atom)
	{
		AStateDefinition stateDef = atom.getAssignments().get(0).getStateDefinition();
		if (stateDef == null){
			return extractInv(atom);
		}
		String stateName = getStateName(stateDef);
		List<PExp> arglist = new Vector<PExp>();
		for (AFieldField f : stateDef.getFields())
		{
			arglist.add(getVarExp(f.getTagname().clone(), f.getType()));
		}
		PExp mkExp = AstExpressionFactory.newAMkTypeExp(new LexNameToken("", stateName, null), stateDef.getRecordType().clone(), arglist);
		PExp invApplyExp = getApplyExp(getVarExp(new LexNameToken("", "inv_"
				+ stateName, null)), new ABooleanBasicType(), mkExp);
		return invApplyExp;
	}

	private String getStateName(PDefinition stateDef)
	{
		return stateDef.getName().getName();
	}

	private PExp extractInv(AAtomicStm atom)
	{
		AAssignmentStm x = atom.getAssignments().get(0);
		if (x.getClassDefinition() != null)
		{
			return invDefs(x.getClassDefinition());
		} else
		{
			return invDefs(x.getStateDefinition());
		}
	}

	public StateInvariantObligation(AImplicitOperationDefinition def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.STATE_INV, ctxt, def.getLocation(), af);
		assistantFactory = af;

		if (def.getClassDefinition() == null)
		{
			stitch = invDefs(def.getClassDefinition());
		} else
		{
			stitch = invDefs(def.getStateDefinition());
		}

		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
		// valuetree.setContext(ctxt.getContextNodeList());
	}

	private PExp invDefs(SClassDefinition def)
	{
		PExp root = null;

		for (PDefinition d : assistantFactory.createSClassDefinitionAssistant().getInvDefs(def.clone()))
		{
			AClassInvariantDefinition cid = (AClassInvariantDefinition) d;
			root = makeAnd(root, cid.getExpression().clone());
		}

		return root;
	}

	private PExp invDefs(PDefinition def)
	{

		if (def instanceof AStateDefinition)
		{
			return ((AStateDefinition) def).getInvdef().getBody();
		} else
		{

			PExp root = null;

			for (PDefinition d : assistantFactory.createSClassDefinitionAssistant().getInvDefs((SClassDefinition) def))
			{
				AClassInvariantDefinition cid = (AClassInvariantDefinition) d;
				root = makeAnd(root, cid.getExpression().clone());
			}

			return root;
		}
	}

}
