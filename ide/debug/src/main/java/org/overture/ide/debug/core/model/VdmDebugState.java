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
package org.overture.ide.debug.core.model;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Assert;

/**
 * State controller for IDebugTarget and IThreads
 * 
 * @author kela
 */
public class VdmDebugState
{
	private List<DebugState> states = new Vector<DebugState>();

	/**
	 * Legal states
	 * 
	 * @author kela
	 */
	public enum DebugState
	{
		Terminated, Suspended, Disconnected, IsStepping, Resumed, Deadlocked
	};

	public VdmDebugState(DebugState initialState)
	{
		if (initialState != null)
		{
			this.states.add(initialState);
		}
	}

	/**
	 * Sets a new state, an Assert.IsLegal is asserted if the given state is not valid based on the current state
	 * 
	 * @param newState
	 *            the new state to change into
	 */
	public synchronized void setState(DebugState newState)
	{
		if (!states.contains(newState))
		{
			switch (newState)
			{
				case Disconnected:
					Assert.isLegal(canChange(DebugState.Disconnected), "Cannot disconnect a terminated state");
				case Terminated:
					Assert.isLegal(canChange(DebugState.Terminated), "Cannot terminate a terminated state");
					states.clear();
					states.add(newState);
					break;
				case Suspended:
					Assert.isLegal(canChange(DebugState.Suspended), "Can only suspend if resumed");
					states.remove(DebugState.Resumed);
					states.add(newState);
					break;
				case IsStepping:
					Assert.isLegal(canChange(DebugState.IsStepping), "Cannot step if not suspended");
					states.add(newState);
					break;
				case Resumed:
					Assert.isLegal(canChange(DebugState.Resumed), "Cannot resume in a terminated state");
					if (states.contains(DebugState.IsStepping))
					{
						states.clear();
						states.add(DebugState.IsStepping);
					} else
					{
						states.clear();
					}
					states.add(newState);
					break;
				case Deadlocked:
					states.add(newState);
					break;
			}
		}
	}

	/**
	 * Checks the current state
	 * 
	 * @param state
	 *            the state to check for
	 * @return true if in the requested state else false
	 */
	public synchronized boolean inState(DebugState state)
	{
		return states.contains(state);
	}

	/**
	 * Checks if a change to the newState is allowed
	 * 
	 * @param newState
	 *            the new state requested
	 * @return true if allowed else false
	 */
	public synchronized boolean canChange(DebugState newState)
	{
		switch (newState)
		{
			case Disconnected:
				return !inState(DebugState.Terminated)
						&& !inState(DebugState.Disconnected);
			case Terminated:
				return !inState(DebugState.Terminated);
			case Suspended:
				return inState(DebugState.Resumed);// states.size()==1 &&
			case IsStepping:
				return (!inState(DebugState.Terminated)
						|| !inState(DebugState.Disconnected) || !inState(DebugState.Deadlocked))
						&& inState(DebugState.Suspended);
			case Resumed:
				return states.size() == 0 || inState(DebugState.IsStepping)
						|| inState(DebugState.Suspended);
			default:
				return false;
		}
	}

	@Override
	public String toString()
	{
		StringBuffer string = new StringBuffer();
		for (DebugState s : states)
		{
			string.append(s);
			string.append(" ");
		}
		return string.toString();
	}
}
