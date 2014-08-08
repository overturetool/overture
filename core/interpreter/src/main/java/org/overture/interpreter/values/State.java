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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.config.Settings;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;

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

		for (AFieldField f : definition.getFields())
		{
			fieldvalues.add(new NameValuePair(f.getTagname(), UpdatableValue.factory(new ValueListenerList(this))));
		}

		ARecordInvariantType rt = (ARecordInvariantType) definition.getRecordType();
		this.context = new Context(Interpreter.getInstance().getAssistantFactory(), definition.getLocation(), "module state", null);
		this.recordValue = UpdatableValue.factory(new RecordValue(rt, fieldvalues, context), new ValueListenerList(this));

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

				if (!definition.getCanBeExecuted()
						|| !(definition.getInitExpression() instanceof AEqualsBinaryExp))
				{
					throw new ValueException(4144, "State init expression cannot be executed", globals);
				}

				AEqualsBinaryExp ee = (AEqualsBinaryExp) definition.getInitExpression();
				ee.getLocation().hit();
				ee.getLeft().getLocation().hit();
				Value v = ee.getRight().apply(VdmRuntime.getExpressionEvaluator(), globals);

				if (!(v instanceof RecordValue))
				{
					throw new ValueException(4144, "State init expression cannot be executed", globals);
				}

				RecordValue iv = (RecordValue) v;

				for (AFieldField f : definition.getFields())
				{
					Value sv = context.get(f.getTagname());
					sv.set(ee.getLocation(), iv.fieldmap.get(f.getTag()), globals);
				}
			}

			doInvariantChecks = true;
			changedValue(null, null, globals);
		}
		// catch (ValueException e)
		// {
		// throw new ContextException(e, definition.getLocation());
		// }
		catch (AnalysisException e)
		{
			if (e instanceof ValueException)
			{
				VdmRuntimeError.abort(definition.getLocation(), (ValueException) e);
			}

		} finally
		{
			doInvariantChecks = true;
		}
	}

	public Context getContext()
	{
		return context;
	}

	public void changedValue(ILexLocation location, Value changed, Context ctxt)
			throws AnalysisException
	{
		if (doInvariantChecks
				&& VdmRuntime.getNodeState(definition).invfunc != null
				&& Settings.invchecks)
		{
			if (location == null)
			{
				location = VdmRuntime.getNodeState(definition).invfunc.body.getLocation();
			}

			try
			{
				if (!VdmRuntime.getNodeState(definition).invfunc.eval(VdmRuntime.getNodeState(definition).invfunc.location, recordValue, ctxt).boolValue(ctxt))
				{
					throw new ContextException(4131, "State invariant violated: "
							+ VdmRuntime.getNodeState(definition).invfunc.name, location, ctxt);
				}
			} catch (ValueException e)
			{
				throw new ContextException(e, location);
			}
		}
	}
}
