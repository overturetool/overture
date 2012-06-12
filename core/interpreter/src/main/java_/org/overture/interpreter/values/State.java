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

package org.overture.interpreter.values;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.types.AFieldField;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;


public class State implements ValueListener
{
	public final AStateDefinition definition;
	public final UpdatableValue recordValue;
	public final Context context;

	public boolean doInvariantChecks = true;

	public State(AStateDefinition definition)
	{
		this.definition = definition;
		NameValuePairList fieldvalues = new NameValuePairList();

		for (AFieldField f: definition.fields)
		{
			fieldvalues.add(new NameValuePair(f.tagname,
				UpdatableValue.factory(new ValueListenerList(this))));
		}

		RecordType rt = (RecordType)definition.getType();
		this.recordValue = UpdatableValue.factory(new RecordValue(rt, fieldvalues),
			new ValueListenerList(this));

		this.context = new Context(definition.location, "module state", null);
		this.context.put(definition.name, recordValue);
		this.context.putList(fieldvalues);
	}

	public void initialize(Context globals)
	{
		try
		{
			// We can't check the invariant while we're initializing fields
			doInvariantChecks = false;

			if (definition.initPattern != null)
			{
				// Note that we don't call the initfunc FunctionValue. This is
				// so that calls to init_sigma can test their arguments without
				// changing state. See StateInitExpression.

				if (!definition.canBeExecuted ||
					!(definition.initExpression instanceof EqualsExpression))
				{
					throw new ValueException(
						4144, "State init expression cannot be executed", globals);
				}

				AEqualsBinaryExp ee = (AEqualsBinaryExp)definition.initExpression;
				ee.location.hit();
				ee.left.location.hit();
				Value v = ee.right.eval(globals);

				if (!(v instanceof RecordValue))
				{
					throw new ValueException(
						4144, "State init expression cannot be executed", globals);
				}

				RecordValue iv = (RecordValue)v;

				for (AFieldField f: definition.fields)
				{
					Value sv = context.get(f.tagname);
					sv.set(ee.location, iv.fieldmap.get(f.tag), globals);
				}
			}

			doInvariantChecks = true;
			changedValue(null, null, globals);
		}
		catch (ValueException e)
		{
			throw new ContextException(e, definition.location);
		}
		finally
		{
			doInvariantChecks = true;
		}
	}

	public Context getContext()
	{
		return context;
	}

	public void changedValue(LexLocation location, Value changed, Context ctxt)
	{
		if (doInvariantChecks && definition.invfunc != null && Settings.invchecks)
		{
			if (location == null)
			{
				location = definition.invfunc.body.location;
			}

    		try
    		{
    			if (!definition.invfunc.eval(
    				definition.invfunc.location, recordValue, ctxt).boolValue(ctxt))
    			{
    				throw new ContextException(
    					4131, "State invariant violated: " + definition.invfunc.name, location, ctxt);
    			}
    		}
    		catch (ValueException e)
    		{
    			throw new ContextException(e, location);
    		}
		}
	}
}
