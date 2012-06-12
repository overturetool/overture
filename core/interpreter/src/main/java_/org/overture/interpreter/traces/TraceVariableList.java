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

import java.util.Vector;

import org.overture.interpreter.runtime.Context;


public class TraceVariableList extends Vector<TraceVariable>
{
	private static final long serialVersionUID = 1L;

	public TraceVariableList()
	{
		super();
	}

	public TraceVariableList(Context ctxt, DefinitionList localDefs)
	{
		Environment local = new FlatEnvironment(localDefs);

		for (LexNameToken key: ctxt.keySet())
		{
			Value value = ctxt.get(key);
			Definition d = local.findName(key, NameScope.NAMES);
			add(new TraceVariable(key.location, key, value, d.getType()));
		}
	}

	public CallSequence getVariables()
	{
		CallSequence seq = new CallSequence();

		for (TraceVariable var: this)
		{
			seq.add(new TraceVariableStatement(var));
		}

		return seq;
	}
}
