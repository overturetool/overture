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

	/**
	 * Sets a new state, an Assert.IsLegal is asserted if the given state is not valid based on the current state
	 * 
	 * @param newState
	 *            the new state to change into
	 */
	public void setState(DebugState newState)
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
					states.clear();
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
	public boolean inState(DebugState state)
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
	public boolean canChange(DebugState newState)
	{
		switch (newState)
		{
			case Disconnected:
				return !inState(DebugState.Terminated) && !inState(DebugState.Disconnected);
			case Terminated:
				return !inState(DebugState.Terminated);
			case Suspended:
				return states.size()==1 && inState(DebugState.Resumed);
			case IsStepping:
				return (!inState(DebugState.Terminated)||!inState(DebugState.Disconnected)||!inState(DebugState.Deadlocked)) && inState(DebugState.Suspended);
			case Resumed:
				return states.size()==0 || inState(DebugState.IsStepping) ||inState(DebugState.Suspended);
			default:
				return false;
		}
	}
}
