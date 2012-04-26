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

import org.overture.interpreter.ast.definitions.AStateDefinitionInterpreter;
import org.overture.interpreter.ast.expressions.AEqualsBinaryExpInterpreter;
import org.overture.interpreter.ast.node.ExternalNodeInterpreter;
import org.overture.interpreter.ast.types.AFieldFieldInterpreter;
import org.overture.interpreter.ast.types.ARecordInvariantTypeInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.vdmj.Settings;


import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;


public class State implements ValueListener, ExternalNodeInterpreter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final AStateDefinitionInterpreter definition;
	public final UpdatableValue recordValue;
	public final Context context;

	public boolean doInvariantChecks = true;

	public State(AStateDefinitionInterpreter definition)
	{
		this.definition = definition;
		NameValuePairList fieldvalues = new NameValuePairList();

		for (AFieldFieldInterpreter f: definition.getFields())
		{
			fieldvalues.add(new NameValuePair(f.getTagname(),
				UpdatableValue.factory(new ValueListenerList(this))));
		}

		ARecordInvariantTypeInterpreter rt = (ARecordInvariantTypeInterpreter)definition.getType();
		this.recordValue = UpdatableValue.factory(new RecordValue(rt, fieldvalues),
			new ValueListenerList(this));

		this.context = new Context(definition.getLocation(), "module state", null);
		this.context.put(definition.getName(), recordValue);
		this.context.putList(fieldvalues);
	}

	public void initialize(Context globals)
	{
		try
		{
			// We can't check the invariant while we're initializing fields
			doInvariantChecks = false;

			if (definition.getInitPattern() != null)
			{
				// Note that we don't call the initfunc FunctionValue. This is
				// so that calls to init_sigma can test their arguments without
				// changing state. See StateInitExpression.

				if (!definition.getCanBeExecuted() ||
					!(definition.getInitExpression() instanceof AEqualsBinaryExpInterpreter))
				{
					throw new ValueException(
						4144, "State init expression cannot be executed", globals);
				}

				AEqualsBinaryExpInterpreter ee = (AEqualsBinaryExpInterpreter)definition.getInitExpression();
				ee.getLocation().hit();
				ee.getLeft().getLocation().hit();
				Value v = ee.getRight().apply(Value.evaluator,globals);

				if (!(v instanceof RecordValue))
				{
					throw new ValueException(
						4144, "State init expression cannot be executed", globals);
				}

				RecordValue iv = (RecordValue)v;

				for (AFieldFieldInterpreter f: definition.getFields())
				{
					Value sv = context.get(f.getTagname());
					sv.set(ee.getLocation(), iv.fieldmap.get(f.getTag()), globals);
				}

				doInvariantChecks = true;
				changedValue(ee.getLocation(), null, globals);
			}
		}
		catch (ValueException e)
		{
			throw new ContextException(e, definition.getLocation());
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
		if (doInvariantChecks && definition.getInvfunc() != null && Settings.invchecks)
		{
    		try
    		{
    			if (!definition.getInvfunc().eval(
    				definition.getInvfunc().location, recordValue, ctxt).boolValue(ctxt))
    			{
    				throw new ContextException(
    					4131, "State invariant violated: " + definition.getInvfunc().name, location, ctxt);
    			}
    		}
    		catch (ValueException e)
    		{
    			throw new ContextException(e, location);
    		}
		}
	}
	
	@Override
	public Object clone() 
	{
		return null;//TODO
	}
}
