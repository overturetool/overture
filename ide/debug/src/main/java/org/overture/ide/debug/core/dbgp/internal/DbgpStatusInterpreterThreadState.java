/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.dbgp.internal;

import java.util.Map;
import java.util.TreeMap;

import org.overture.ide.debug.core.dbgp.IDbgpStatusInterpreterThreadState;

public class DbgpStatusInterpreterThreadState implements
		IDbgpStatusInterpreterThreadState
{
	private static final Map<String, InterpreterThreadStatus> stateParser = new TreeMap<String, InterpreterThreadStatus>(String.CASE_INSENSITIVE_ORDER);

	static
	{
		stateParser.put("CREATED", InterpreterThreadStatus.CREATED);
		stateParser.put("RUNNABLE", InterpreterThreadStatus.RUNNABLE);
		stateParser.put("RUNNING", InterpreterThreadStatus.RUNNING);
		stateParser.put("LOCKING", InterpreterThreadStatus.LOCKING);
		stateParser.put("WAITING", InterpreterThreadStatus.WAITING);
		stateParser.put("TIMESTEP", InterpreterThreadStatus.TIMESTEP);
		stateParser.put("COMPLETE", InterpreterThreadStatus.COMPLETE);
	}

	public static IDbgpStatusInterpreterThreadState parse(int id, String name,
			String state)
	{
		return new DbgpStatusInterpreterThreadState(id, name, stateParser.get(state));
	}

	private final int id;

	private final String name;

	private final InterpreterThreadStatus state;

	public DbgpStatusInterpreterThreadState(int id, String name,
			InterpreterThreadStatus state)
	{
		if (name == null)
		{
			throw new IllegalArgumentException();
		}

		this.id = id;
		this.name = name;
		this.state = state;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public InterpreterThreadStatus getState()
	{
		return state;
	}

	@Override
	public String toString()
	{
		return "Id: " + id + " Name: " + name + " State: " + state;
	}
}
