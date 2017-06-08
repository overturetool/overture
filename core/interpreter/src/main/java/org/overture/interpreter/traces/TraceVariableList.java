/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overture.interpreter.traces;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatEnvironment;

public class TraceVariableList extends Vector<TraceVariable>
{
	private static final long serialVersionUID = 1L;

	public TraceVariableList()
	{
		super();
	}

	public TraceVariableList(Context ctxt, List<PDefinition> localDefs)
	{
		Environment local = new FlatEnvironment(ctxt.assistantFactory, localDefs);

		for (ILexNameToken key : ctxt.keySet())
		{
			Value value = ctxt.get(key);
			PDefinition d = local.findName(key, NameScope.NAMES);
			boolean clone = false;

			if (value.isType(ObjectValue.class))
			{
				ObjectValue obj = (ObjectValue) value.deref();
				ObjectValue self = ctxt.getSelf();

				// We have to clone new objects that were created within the trace,
				// while using other (local instance variable) objects unchanged.
				clone = self != null
						&& obj.objectReference > self.objectReference;
			}

			add(new TraceVariable(key.getLocation(), key, value, d.getType(), clone));
		}
	}

	public CallSequence getVariables()
	{
		CallSequence seq = new CallSequence();

		for (TraceVariable var : this)
		{
			seq.add(new TraceVariableStatement(var));
		}

		return seq;
	}
}
