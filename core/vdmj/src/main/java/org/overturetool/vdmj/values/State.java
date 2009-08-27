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

package org.overturetool.vdmj.values;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.StateDefinition;
import org.overturetool.vdmj.expressions.EqualsExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.RecordType;

public class State implements ValueListener
{
	public final StateDefinition definition;
	public final UpdatableValue recordValue;
	public final Context context;

	public boolean doInvariantChecks = true;

	public State(StateDefinition definition)
	{
		this.definition = definition;
		NameValuePairList fieldvalues = new NameValuePairList();

		for (Field f: definition.fields)
		{
			fieldvalues.add(new NameValuePair(f.tagname, UpdatableValue.factory(this)));
		}

		RecordType rt = (RecordType)definition.getType();
		this.recordValue = UpdatableValue.factory(new RecordValue(rt, fieldvalues), this);

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

				EqualsExpression ee = (EqualsExpression)definition.initExpression;
				RecordValue iv = (RecordValue)ee.right.eval(globals);

				for (Field f: definition.fields)
				{
					Value sv = context.get(f.tagname);
					sv.set(ee.location, iv.fieldmap.get(f.tag), globals);
				}

				doInvariantChecks = true;
				changedValue(ee.location, null, globals);
			}
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
