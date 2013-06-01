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

import java.util.List;
import java.util.Vector;

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
import org.overturetool.vdmj.values.UpdatableValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueListenerList;
import org.overturetool.vdmj.values.VoidValue;


public class AtomicStatement extends Statement
{
	private static final long serialVersionUID = 1L;

	public final List<AssignmentStatement> assignments;

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
		
		int size = assignments.size();
		ValueList targets = new ValueList(size);
		ValueList values = new ValueList(size);
		
		// Rather than execute the assignment statements directly, we calculate the
		// Updateable values that would be affected, and the new values to put in them.
		// Note that this does not provoke any invariant checks (other than those that
		// may be present in the RHS expression of each assignment).
		
		for (AssignmentStatement stmt: assignments)
		{
			try
			{
				stmt.location.hit();
				targets.add(stmt.target.eval(ctxt));
				values.add(stmt.exp.eval(ctxt).convertTo(stmt.targetType, ctxt));
			}
			catch (ValueException e)
			{
				abort(e);
			}
		}
		
		// We make the assignments atomically by turning off thread swaps and time
		// then temporarily removing the listener lists from each Updateable target.
		// Then, when all assignments have been made, we check the invariants by
		// passing the updated values to each listener list, as the assignment would have.
		// Finally, we re-enable the thread swapping and time stepping, before returning
		// a void value.
		
		ctxt.threadState.setAtomic(true);
		List<ValueListenerList> listenerLists = new Vector<ValueListenerList>(size);

		for (int i = 0; i < size; i++)
		{
			UpdatableValue target = (UpdatableValue) targets.get(i);
			listenerLists.add(target.listeners);
			target.listeners = null;
			target.set(location, values.get(i), ctxt);	// No invariant listeners
			target.listeners = listenerLists.get(i);
		}
		
		for (int i = 0; i < size; i++)
		{
			ValueListenerList listeners = listenerLists.get(i);
			
			if (listeners != null)
			{
				listeners.changedValue(location, values.get(i), ctxt);
			}
		}

		ctxt.threadState.setAtomic(false);
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
}
