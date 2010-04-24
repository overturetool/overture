package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugException;

/**
 * The IVdmExecution interface is used to signal that a certain thread or debug target should perform an action which is
 * related to a specific state change but not change the state
 * 
 * @author kela
 */
public interface IVdmExecution
{
	/**
	 * Resume the given thread
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void doResume(Object source) throws DebugException;
	/**
	 * Suspend the given thread
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void doSuspend(Object source) throws DebugException;
	/**
	 * Step into in the given thread
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void doStepInto(Object source) throws DebugException;
	/**
	 * Step over in the given thread
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void doStepOver(Object source) throws DebugException;
	/**
	 * Step return in the given thread
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void doStepReturn(Object source) throws DebugException;
	/**
	 * Terminate the given thread
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void doTerminate(Object source) throws DebugException;
	
	/**
	 * Handle pre suspend processing
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void doPreSuspendRequest(Object source) throws DebugException;
	
	
	/**
	 * Sets the given thread to deadlocked
	 * @param source the source who requested this change to occur. This can be used to check if a cycle is occurring.
	 * @throws DebugException
	 */
	void markDeadlocked(Object source)throws DebugException;
}
