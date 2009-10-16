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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.AsyncThread;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.VDMThread;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueSet;
import org.overturetool.vdmj.values.VoidValue;

public class StartStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final Expression objects;

	public StartStatement(LexLocation location, Expression obj)
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
		if (Settings.dialect == Dialect.VDM_RT)
		{
			return evalRT(ctxt);
		}
		else
		{
			return evalPP(ctxt);
		}
	}

	private Value evalRT(Context ctxt)
	{
		try
		{
			Value value = objects.eval(ctxt);

			if (value.isType(SetValue.class))
			{
				ValueSet set = value.setValue(ctxt);

				for (Value v: set)
				{
					ObjectValue target = v.objectValue(ctxt);
					OperationValue op = target.getThreadOperation(ctxt);

					startRT(target, op);
				}
			}
			else
			{
				ObjectValue target = value.objectValue(ctxt);
				OperationValue op = target.getThreadOperation(ctxt);

				startRT(target, op);
			}

			return new VoidValue();
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	// Note that RT does not use VDMThreads at all...

	private void startRT(ObjectValue target, OperationValue op)
		throws ValueException
	{
		if (op.body instanceof PeriodicStatement)
		{
    		RootContext global = ClassInterpreter.getInstance().initialContext;
    		Context ctxt = new ObjectContext(op.name.location, "async", global, target);

			PeriodicStatement ps = (PeriodicStatement)op.body;
			OperationValue pop = ctxt.lookup(ps.opname).operationValue(ctxt);

			long period = ps.values[0];
			long jitter = ps.values[1];
			long delay  = ps.values[2];
			long offset = ps.values[3];

			new AsyncThread(target, pop, new ValueList(), period, jitter, delay, offset, 0).start();
		}
		else
		{
			new AsyncThread(target, op, new ValueList(), 0, 0, 0, 0, 0).start();
		}
	}

	private Value evalPP(Context ctxt)
	{
		try
		{
			Value value = objects.eval(ctxt);

			if (value.isType(SetValue.class))
			{
				ValueSet set = value.setValue(ctxt);

				for (Value v: set)
				{
					new VDMThread(location, v.objectValue(ctxt), ctxt).start();
				}
			}
			else
			{
				new VDMThread(location, value.objectValue(ctxt), ctxt).start();
			}

			return new VoidValue();
		}
		catch (ValueException e)
		{
			return abort(e);
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
		return "start";
	}

	@Override
	public String toString()
	{
		return kind() + "(" + objects + ")";
	}
}
