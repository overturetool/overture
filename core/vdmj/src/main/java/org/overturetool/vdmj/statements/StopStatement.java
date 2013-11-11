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

package org.overturetool.vdmj.statements;

import java.util.List;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.ObjectThread;
import org.overturetool.vdmj.scheduler.PeriodicThread;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;
import org.overturetool.vdmj.values.VoidValue;

public class StopStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression objects;

	public StopStatement(LexLocation location, Expression obj)
	{
		super(location);
		this.objects = obj;
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		Type type = objects.typeCheck(env, null, scope);

		if (type.isSet())
		{
			SetType set = type.getSet();

			if (!set.setof.isClass())
			{
				objects.report(3235, "Expression is not a set of object references");
			}
			else
			{
				ClassType ctype = set.setof.getClassType();

				if (ctype.classdef.findThread() == null)
				{
					objects.report(3236, "Class does not define a thread");
				}
			}
		}
		else if (type.isClass())
		{
			ClassType ctype = type.getClassType();

			if (ctype.classdef.findThread() == null)
			{
				objects.report(3237, "Class does not define a thread");
			}
		}
		else
		{
			objects.report(3238, "Expression is not an object reference or set of object references");
		}

		return new VoidType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
			Value value = objects.eval(ctxt);

			if (value.isType(SetValue.class))
			{
				ValueSet set = value.setValue(ctxt);

				for (Value v: set)
				{
					ObjectValue target = v.objectValue(ctxt);
					stop(target, ctxt);
				}
			}
			else
			{
				ObjectValue target = value.objectValue(ctxt);
				stop(target, ctxt);
			}

			// Cause a reschedule so that this thread is stopped, if necessary
			ISchedulableThread th = BasicSchedulableThread.getThread(Thread.currentThread());
			th.reschedule(ctxt, location);
			
			return new VoidValue();
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	private void stop(ObjectValue target, Context ctxt) throws ValueException
	{
		List<ISchedulableThread> threads = BasicSchedulableThread.findThreads(target);
		int count = 0;
		
		if (target.getCPU() != ctxt.threadState.CPU)
		{
			throw new ContextException(4161,
					"Cannot stop object " + target.objectReference +
					" on CPU " + target.getCPU().getName() +
					" from CPU " + ctxt.threadState.CPU,
					location, ctxt);
		}
		
		for (ISchedulableThread th: threads)
		{
			if (th instanceof ObjectThread || th instanceof PeriodicThread)
			{
				if (th.stopThread())	// This may stop current thread at next reschedule
				{
					count++;
				}
			}
		}

		if (count == 0)
		{
			throw new ContextException(4160,
				"Object #" + target.objectReference + " is not running a thread to stop", location, ctxt);
		}
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return objects.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return objects.getProofObligations(ctxt);
	}

	@Override
	public String kind()
	{
		return "stop";
	}

	@Override
	public String toString()
	{
		return kind() + "(" + objects + ")";
	}
}
