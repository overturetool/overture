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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.ClassInvariantListener;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.State;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;


public class AtomicStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	private static final Set<Thread> atomicThreads = new HashSet<Thread>();

	public final List<AssignmentStatement> assignments;
	private StateDefinition statedef = null;

	public AtomicStatement(LexLocation location, List<AssignmentStatement> assignments)
	{
		super(location);
		this.assignments = assignments;
	}

	@Override
	public String toString()
	{
		return "atomic (" + Utils.listToString(assignments) + ")";
	}

	@Override
	public String kind()
	{
		return "atomic";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		statedef = env.findStateDefinition();

		for (AssignmentStatement stmt: assignments)
		{
			stmt.typeCheck(env, scope);
		}

		return new VoidType(location);
	}

	@Override
	public Statement findStatement(int lineno)
	{
		Statement found = super.findStatement(lineno);
		if (found != null) return found;

		for (AssignmentStatement stmt: assignments)
		{
			found = stmt.findStatement(lineno);
			if (found != null) break;
		}

		return found;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = null;

		for (AssignmentStatement stmt: assignments)
		{
			found = stmt.findExpression(lineno);
			if (found != null) break;
		}

		return found;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		State state = null;
		ClassInvariantListener listener = null;

		if (statedef != null)
		{
			state = statedef.getState();
			state.doInvariantChecks = false;
		}
		else
		{
			ObjectValue self = ctxt.getSelf();

			if (self != null && self.invlistener != null)
			{
				listener = self.invlistener;
				listener.doInvariantChecks = false;
			}
		}
		
		addAtomicThread();

		for (AssignmentStatement stmt: assignments)
		{
			stmt.eval(ctxt);
		}
		
		removeAtomicThread();

		// Now run through the assignments again after the atomic lock is lifted to
		// check that all the type invariants still hold afterwards. Note that we pass
		// a clone of the value to "set" to force it to check.
		
		for (AssignmentStatement stmt: assignments)
		{
			try
			{
				Value newval = stmt.target.eval(ctxt);
				newval.set(stmt.location, (Value)newval.clone(), ctxt);
			}
			catch (ValueException e)
			{
				abort(e);
			}
		}
		
		if (state != null)
		{
			state.doInvariantChecks = true;
			state.changedValue(location, null, ctxt);
		}
		else if (listener != null)
		{
			listener.doInvariantChecks = true;
			listener.changedValue(location, null, ctxt);
		}

		return new VoidValue();
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (AssignmentStatement stmt: assignments)
		{
			obligations.addAll(stmt.getProofObligations(ctxt));
		}

		return obligations;
	}
	
	/*
	 * State invariants are switched on/off in the exec method directly, but
	 * type invariants may be affected in arbitrary places, so these methods
	 * record the (per thread) fact that we are inside an atomic statement.
	 */
	
	private synchronized void addAtomicThread()
	{
		atomicThreads.add(Thread.currentThread());
	}
	
	private synchronized void removeAtomicThread()
	{
		atomicThreads.remove(Thread.currentThread());
	}
	
	public synchronized static boolean insideAtomic()
	{
		return atomicThreads.contains(Thread.currentThread());
	}
}
