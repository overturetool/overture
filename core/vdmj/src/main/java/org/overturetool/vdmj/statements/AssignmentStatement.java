/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.StateInvariantObligation;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class AssignmentStatement extends Statement
{
	private static final long serialVersionUID = 1L;

	public final Expression exp;
	public final StateDesignator target;
	public Type targetType;
	public Type expType;
	public ClassDefinition classDefinition = null;
	public StateDefinition stateDefinition = null;
	public boolean inConstructor = false;

	public AssignmentStatement(
		LexLocation location, StateDesignator target, Expression exp)
	{
		super(location);
		this.exp = exp;
		this.target = target;
	}

	@Override
	public String toString()
	{
		return target + " := " + exp;
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		targetType = target.typeCheck(env);
		expType = exp.typeCheck(env, null, scope);

		if (!TypeComparator.compatible(targetType, expType))
		{
			report(3239, "Incompatible types in assignment");
			detail2("Target", targetType, "Expression", expType);
		}

		classDefinition = env.findClassDefinition();
		stateDefinition = env.findStateDefinition();

		Definition encl = env.getEnclosingDefinition();

		if (encl != null)
		{
			if (encl instanceof ExplicitOperationDefinition)
			{
				ExplicitOperationDefinition op = (ExplicitOperationDefinition)encl;
				inConstructor = op.isConstructor;
			}
			else if (encl instanceof ImplicitOperationDefinition)
			{
				ImplicitOperationDefinition op = (ImplicitOperationDefinition)encl;
				inConstructor = op.isConstructor;
			}
		}

		return new VoidType(location);
	}

	@Override
	public TypeSet exitCheck()
	{
		// TODO We don't know what an expression call will raise
		return new TypeSet(new UnknownType(location));
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		Value newval = exp.eval(ctxt);
		Value oldval = target.eval(ctxt);

		try
		{
			oldval.set(location, newval.convertTo(targetType, ctxt), ctxt);
		}
		catch (ValueException e)
		{
			abort(e);
		}

		if (Settings.dialect == Dialect.VDM_RT)
		{
			ObjectValue self = ctxt.getSelf();	// May be a static

			// The showtrace plugin does not like "quotes", nor does it
			// have a \" type convention, so we substitute for apostrophes.
			String noquotes = newval.toString().replaceAll("\\\"", "\'");

			if (self == null)
			{
    			RTLogger.log(
    				"InstVarChange -> instnm: \"" + target.toString() + "\"" +
    				" val: \"" + noquotes + "\"" +
    				" objref: nil" +
    				" id: " + Thread.currentThread().getId());
			}
			else
			{
    			RTLogger.log(
    				"InstVarChange -> instnm: \"" + target.toString() + "\"" +
    				" val: \"" + noquotes + "\"" +
    				" objref: " + self.objectReference +
    				" id: " + Thread.currentThread().getId());
			}
		}

		return new VoidValue();
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return exp.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		if (!inConstructor &&
			(classDefinition != null && classDefinition.invariant != null) ||
			(stateDefinition != null && stateDefinition.invExpression != null))
		{
			obligations.add(new StateInvariantObligation(this, ctxt));
		}

		obligations.addAll(exp.getProofObligations(ctxt));

		if (!TypeComparator.isSubType(expType, targetType))
		{
			obligations.add(
				new SubTypeObligation(exp, targetType, expType, ctxt));
		}

		return obligations;
	}

	@Override
	public String kind()
	{
		return "assignment";
	}
}
